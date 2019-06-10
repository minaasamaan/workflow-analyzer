package com.mycompany.analyzer.core.pipeline;

import com.mycompany.analyzer.core.dto.ImportedItemDto;
import com.mycompany.analyzer.core.entity.BusinessEntity;
import com.mycompany.analyzer.core.entity.PipelineCandidate;
import com.mycompany.analyzer.core.repository.BusinessEntityRepository;
import com.mycompany.analyzer.core.utils.ProcessingCache;
import com.mycompany.analyzer.importer.errors.data.BusinessError;
import com.mycompany.analyzer.importer.errors.data.BusinessErrorsCollection;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * Parent of all processors, which is taking Dto, validating inconsistencies & producing a processed PipelineCandidate.
 * PipelineCandidate could be either a valid, enriched business entity, or simply a collection of (syntactic/semantic) errors.
 * @see AbstractItemReader
 * @param <Dto> Input Dto, created by readers
 * @param <E> Input business entity, injected by readers.
 */
public abstract class AbstractItemProcessor<Dto extends ImportedItemDto<E>, E extends BusinessEntity> implements
                                                                                                      ItemProcessor<Dto, PipelineCandidate> {

    @Autowired
    private Validator validator;

    @Override
    public PipelineCandidate process(Dto item) throws Exception {

        if (!item.getParsingErrorsCollection().isEmpty()) {
            //Assumption: Do not tolerate parsing problems, if record contains any!
            return item.getParsingErrorsCollection();
        }

        BusinessErrorsCollection businessErrorsCollection = new BusinessErrorsCollection(item.getStartsAtLine(),
                                                                                         item.getEndsAtLine());

        //it should be always a BusinessEntity
        E businessEntity = item.getBusinessEntity();

        Set<ConstraintViolation<E>> constraintViolations = validator.validate(businessEntity);

        //check for basic violations (e.g. mandatory fields, etc..)
        if (!constraintViolations.isEmpty()) {
            constraintViolations.forEach(violation -> reportBusinessError(businessEntity.getClass(),
                                                                          violation,
                                                                          businessErrorsCollection));
            return businessErrorsCollection;
        }

        //check if exists on the current Job execution, reject duplicates within same execution!
        if (ProcessingCache.contains(businessEntity.getClass(), businessEntity.getCode())) {
            reportBusinessError(businessEntity.getClass(),
                                String.format("Business entity already exists in the same import with code: %s",
                                              businessEntity.getCode()),
                                businessErrorsCollection);
            return businessErrorsCollection;
        }

        //update cache
        ProcessingCache.put(businessEntity.getClass(), businessEntity);

        //check if exists on DB, then should update
        E existingBusinessEntity = getRepository().findByCode(businessEntity.getCode());
        if (existingBusinessEntity != null) {
            businessEntity.setId(existingBusinessEntity.getId());
        }

        doProcess(businessEntity, businessErrorsCollection);

        //check for violations specific to business entities
        if (!businessErrorsCollection.isEmpty()) {
            return businessErrorsCollection;
        }

        return (PipelineCandidate) businessEntity;
    }

    protected abstract void doProcess(E businessEntity,
                                      BusinessErrorsCollection businessErrorsCollection);

    protected abstract BusinessEntityRepository<E> getRepository();

    protected void reportBusinessError(Class<? extends BusinessEntity> entityClazz,
                                       String constraintViolation,
                                       BusinessErrorsCollection businessErrorsCollection) {
        businessErrorsCollection.getBusinessErrors()
                                .add(new BusinessError(entityClazz.getSimpleName(),
                                                       businessErrorsCollection.getStartsAtLine(),
                                                       businessErrorsCollection.getEndsAtLine(),
                                                       constraintViolation));
    }

    private void reportBusinessError(Class<? extends BusinessEntity> entityClazz,
                                     ConstraintViolation<E> constraintViolation,
                                     BusinessErrorsCollection businessErrorsCollection) {
        final String DELIMITER = " ";
        String violation = constraintViolation.getPropertyPath()
                                              .toString()
                                              .concat(DELIMITER)
                                              .concat(constraintViolation.getMessage());
        reportBusinessError(entityClazz, violation, businessErrorsCollection);
    }
}
