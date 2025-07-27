package com.processor_csv.infraestructure.batch.config;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;

public class FlatFileParseExceptionSkipPolicy implements SkipPolicy {

    public boolean shouldSkip(Throwable t, int skipCount) {
        // Ignorar solo excepciones de parsing
        return t instanceof FlatFileParseException;
    }

    @Override
    public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException {
        return false;
    }
}
