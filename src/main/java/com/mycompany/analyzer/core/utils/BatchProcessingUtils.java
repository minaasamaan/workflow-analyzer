package com.mycompany.analyzer.core.utils;

import com.google.common.collect.ImmutableMap;

import com.mycompany.analyzer.importer.workflow.WorkflowConfigurations;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStreamReader;
import java.util.Map;

/**
 * Utility class providing a convenient way for creating FlatFileItemReader.
 * @see FlatFileItemReader
 * @see WorkflowConfigurations for usage example
 */
public class BatchProcessingUtils {
    private static DelimitedLineTokenizer defaultTokenizer;
    public static DelimitedLineTokenizer dataTokenizer;

    static{
        dataTokenizer = new DelimitedLineTokenizer(":");
        dataTokenizer.setFieldSetFactory(new LineAwareFieldSetFactory());

        defaultTokenizer = new DelimitedLineTokenizer();
        defaultTokenizer.setFieldSetFactory(new LineAwareFieldSetFactory());
    }

    private static final Map<String, LineTokenizer>  commonTokenizerMap= ImmutableMap.of("start*", defaultTokenizer,
                                                                                         "end*", defaultTokenizer,
                                                                                         "*", defaultTokenizer);

    public static FlatFileItemReader<LineAwareFieldSet> newFlatFileItemReader(final String name, final String file, final
                                                                       Map<String, LineTokenizer> tokenizerMap, String header){
        DefaultLineMapper<LineAwareFieldSet> lineMapper = new LineAwareFieldSetMapper();
        lineMapper.setFieldSetMapper(new PassThroughLineAwareFieldSetMapper());

        PatternMatchingCompositeLineTokenizer tokenizer = new PatternMatchingCompositeLineTokenizer();

        tokenizer.setTokenizers(ImmutableMap.<String, LineTokenizer>builder().putAll(tokenizerMap).putAll(commonTokenizerMap).build());

        lineMapper.setLineTokenizer(tokenizer);

        FlatFileItemReader<LineAwareFieldSet> itemReader = new FlatFileItemReaderBuilder<LineAwareFieldSet>()
                .name(name)
                .resource(new ClassPathResource(file))
                .lineMapper(lineMapper)
                .addComment(header)
                .build();

        itemReader.setBufferedReaderFactory((resource, encoding) -> new LineTrimmingBufferedReader(new InputStreamReader(
                resource.getInputStream(),
                encoding)));
        return itemReader;
    }
}
