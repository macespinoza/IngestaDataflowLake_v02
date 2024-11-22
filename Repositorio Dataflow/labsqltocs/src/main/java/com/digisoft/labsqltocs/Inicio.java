/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.digisoft.labsqltocs;

/**
 *
 * @author maces
 */

import com.google.api.services.bigquery.model.TableRow;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.TextIO;

import org.apache.beam.sdk.io.gcp.bigquery.TableRowJsonCoder;
import org.apache.beam.sdk.io.jdbc.JdbcIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFn.ProcessContext;
import org.apache.beam.sdk.transforms.DoFn.ProcessElement;
import org.apache.beam.sdk.transforms.ParDo;

/**
 *
 * @author maces
 */
public class Inicio {

    public static void main(String[] args) {
        
        TimeZone timeZone = TimeZone.getTimeZone("America/Lima");
        TimeZone.setDefault(timeZone);
        System.setProperty("user.timezone", "America/Lima");
        CustomPipelineOptions.custompipelineoptions pipelineOptions =
                PipelineOptionsFactory.fromArgs(args).as(CustomPipelineOptions.custompipelineoptions .class);
        
        run(pipelineOptions);
    }
    private static PipelineResult run(CustomPipelineOptions.custompipelineoptions options) {
        Pipeline pipeline = Pipeline.create(options);
        pipeline.apply("Extraccion de JDBC",JdbcIO.<TableRow>read()
            .withDataSourceConfiguration(JdbcIO.DataSourceConfiguration.create(
            options.getDriverClassName(),options.getConnectionURL())
              
              .withUsername(options.getUsername())
              .withPassword(options.getPassword()))
              .withQuery(options.getQuery())
              .withCoder(TableRowJsonCoder.of())
              .withRowMapper(CustomPipelineOptions.getResultSetToTableRow()))
                .apply("Convertir a CSV", ParDo.of(new DoFn<TableRow, String>() {

                    private transient boolean headerWritten = false; // Para manejar la cabecera
                    private transient List<String> columnHeaders;

                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        TableRow row = c.element();

                        // Inicializa la cabecera solo una vez
                        if (!headerWritten) {
                            columnHeaders = new ArrayList<>(row.keySet()); // Obtén dinámicamente los nombres de las columnas
                            String header = String.join(",", columnHeaders); // Une los nombres por comas
                            c.output(header); // Escribe la cabecera como primera línea
                            headerWritten = true;
                        }

                        // Escribe la fila como CSV dinámico
                        List<String> values = new ArrayList<>();
                        for (String key : columnHeaders) {
                            Object value = row.get(key);
                            // Maneja valores nulos reemplazándolos con una cadena vacía
                            values.add(value == null ? "" : value.toString().replace(",", "\\,")); // Escapa las comas
                        }
                        String csvRow = String.join(",", values);
                        c.output(csvRow);
                    }
                }))
                .apply("Deposito en GCS", TextIO.write()
                        .to(options.getGcslake())
                        .withSuffix(".csv")
                        .withoutSharding());
         return pipeline.run();
    }
}


