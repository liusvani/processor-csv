package com.processor_csv.infraestructure.batch.listener;

import com.processor_csv.domain.model.User;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;
import jakarta.annotation.PreDestroy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserSkipListener implements SkipListener<User, User>, StepExecutionListener {

    private BufferedWriter errorWriter;
    private final List<String> emailsInvalidos = new ArrayList<>();
    private int totalProccess = 0;
    private int totalErrores = 0;
    private int totalRowSaveDB = 0;
    private int totalRowDeleteFromCsv ;
    private int totalErrorsFromCsv = 0;
    private int totalErrorNotProcess= 0;
    private Instant inicio;
    private static final Logger log = LoggerFactory.getLogger(UserSkipListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        inicio = Instant.now();
        totalProccess = 0;
        totalErrores = 0;
        totalRowSaveDB = 0;
        totalRowDeleteFromCsv = 0;
        totalErrorsFromCsv = 0;
        totalErrorNotProcess = 0;
        emailsInvalidos.clear();

        try {
            Path errorPath = Paths.get("./data/errores_usuarios.csv");
            errorWriter = Files.newBufferedWriter(errorPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            errorWriter.write("firstname,lastname,age,email,occupation,error,timestamp\n");
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear el archivo de errores", e);
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        totalProccess = (int) stepExecution.getReadCount();
        totalErrorNotProcess = (int) stepExecution.getReadSkipCount();
        totalRowSaveDB = totalProccess - totalRowDeleteFromCsv;

        Duration duracion = Duration.between(inicio, Instant.now());

        try (BufferedWriter resumenWriter = Files.newBufferedWriter(
                Paths.get("./data/resumen_batch.csv"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            resumenWriter.write("------------------Reports------------------\n");
            resumenWriter.write("- Total de usuarios procesados sin errores: " + totalProccess + "\n");
            resumenWriter.write("- Total de usuarios no procesados por errores: " + totalErrorNotProcess + "\n");
            resumenWriter.write("- Total de errores en el fichero: " + totalErrorsFromCsv + totalErrorNotProcess +"\n");
            resumenWriter.write("--------------------------------------------------\n");
            resumenWriter.write("--------------- Datos del job: ----------------- \n");
            resumenWriter.write("- ID : " + stepExecution.getJobExecution().getJobId() +"\n");
            resumenWriter.write("- Estado: " + stepExecution.getJobExecution().getStatus() +"\n");
            resumenWriter.write("- Inicio: " + stepExecution.getJobExecution().getCreateTime() +"\n");
            resumenWriter.write("- Version: " + stepExecution.getJobExecution().getVersion() +"\n");
            resumenWriter.write("--------------------------------------------------\n");
            /*for (String email : emailsInvalidos) {
                resumenWriter.write(" - " + email + "\n");
            }*/
            resumenWriter.write("Tiempo de ejecución: " + duracion.toMillis() + " ms\n");
        } catch (IOException e) {
            log.error("Error escribiendo resumen de ejecución", e);
        }

        deleteLineByParse();
        return ExitStatus.COMPLETED;
    }

    @Override
    public void onSkipInProcess(@Nullable User user, @Nullable Throwable t) {
        registrarError(user, buildErrorMap(t, "procesamiento"));
    }

    @Override
    public void onSkipInWrite(@Nullable User user, @Nullable Throwable t) {
        registrarError(user, buildErrorMap(t, "escritura"));
    }

    @Override
    public void onSkipInRead(@Nullable Throwable t) {
        registrarError(null, buildErrorMap(t, "lectura"));
    }

    private Map<String, String> buildErrorMap(@Nullable Throwable t, String etapa) {
        String mensaje = (t != null && t.getMessage() != null) ? t.getMessage() : "Excepción desconocida en " + etapa;
        String campoFallado = inferirCampoFallado(t);
        return Map.of(campoFallado, mensaje);
    }

    public void registrarError(@Nullable User user, Map<String, String> erroresPorCampo) {
        totalErrores += erroresPorCampo.size();



        String email = user != null ? cleanField(user.getEmail()) : "N/A";
        String nombre = user != null ? cleanField(user.getFirstname()) : "N/A";
        String apellidos = user != null ? cleanField(user.getLastname()) : "N/A";
        String occupation = user != null ? cleanField(user.getOccupation()) : "N/A";
        Integer edadvalue = (user != null && user.getAge() != null) ? user.getAge() : 0;

        emailsInvalidos.add(email);

        log.warn("Error escribiendo error de usuario: {}", erroresPorCampo.size());
        totalErrorsFromCsv += erroresPorCampo.size();

        String errorConcat = erroresPorCampo.values().stream()
                .map(e -> e.replace("\"", "'"))
                .collect(Collectors.joining(" | "));

        String linea = String.format("\"%s\",\"%s\",\"%d\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                safe(nombre), safe(apellidos), edadvalue, safe(email),
                safe(occupation), errorConcat, ZonedDateTime.now());
        try {
            errorWriter.write(linea);
            errorWriter.flush();
        } catch (IOException e) {
            log.error("Error escribiendo error de usuario: {}", e.getMessage(), e);
        }
    }

    private String safe(String input) {
        return input == null || input.trim().isEmpty() ? "N/A" : input.replace("\"", "'");
    }

    public static String cleanField(@Nullable String valor) {
        return Optional.ofNullable(valor).orElse("N/A").replace("'", "").trim();
    }

    private String inferirCampoFallado(@Nullable Throwable t) {
        if (t == null || t.getMessage() == null) return "desconocido";
        String msg = t.getMessage().toLowerCase();
        if (msg.contains("parsing")) return "edad";
        if (msg.contains("line") || msg.contains("input") || msg.contains("index out of bounds")) return "estructura de línea";
        if (msg.contains("first") || msg.contains("firstname") || msg.contains("nombre")) return "nombre";
        if (msg.contains("last") || msg.contains("lastname") || msg.contains("apellido")) return "apellido";
        if (msg.contains("email")) return "email";
        if (msg.contains("occupation") || msg.contains("pation")) return "occupation";
        if (msg.contains("age")) return "edad";
        return "desconocido";
    }

    public void deleteLineByParse() {
        Path errorPath = Paths.get("./data/errores_usuarios.csv");
        try {
            List<String> allLines = Files.readAllLines(errorPath);
            if (allLines.isEmpty()) {
                log.warn("⚠️ Archivo errores_usuarios.csv vacío. No hay líneas para filtrar.");
                return;
            }

            String encabezado = allLines.get(0);
            List<String> criteriosExclusion = List.of("parsing error", "estructura de línea", "invalid structure");
            List<String> filtradas = allLines.stream()
                    .skip(1)
                    .filter(line -> criteriosExclusion.stream().noneMatch(line.toLowerCase()::contains))
                    .toList();

            try (BufferedWriter writer = Files.newBufferedWriter(errorPath, StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write(encabezado + "\n");
                for (String linea : filtradas) {
                    writer.write(linea + "\n");
                }
            }

             totalRowDeleteFromCsv = allLines.size() - 1 - filtradas.size();
            log.info("Se eliminaron {} líneas con errores de parseo.", totalRowDeleteFromCsv);
        } catch (IOException e) {
            log.error("Error al filtrar errores CSV: {}", e.getMessage(), e);
        }
    }

    @PreDestroy
    public void closeWriter() {
        if (errorWriter != null) {
            try {
                errorWriter.close();
            } catch (IOException e) {
                log.warn("No se pudo cerrar el archivo de errores correctamente", e);
            }
        }
    }
}
