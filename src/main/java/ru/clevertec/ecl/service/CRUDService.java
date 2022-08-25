package ru.clevertec.ecl.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.ecl.dto.AbstractModel;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.entity.baseentities.Certificate;

/**
 * An interface declaring basic CRUD methods.
 *
 * @author Olga Mailychko
 *
 */
public interface CRUDService<T extends AbstractModel> {
    /**
     * Performs saving logic.
     *
     * @param toSave DTO object containing saving data
     * @return saved object as DTO
     */
    T save(T toSave);
    /**
     * Finds entity by id in storage.
     *
     * @param id id of needed entity
     * @return found entity as DTO object
     */
    T findById(long id);
    /**
     * Finds all entities from storage with given filters.
     *
     * @param filter DTO object containing filtering fields
     * @param pageable {@link Pageable} object storing pagination data
     * @return paged collection of all found entities represented as DTO objects
     * meeting all requirements
     */
    Page<T> getAll(T filter, Pageable pageable);
    /**
     * Method performs removing entity with given id from storage.
     *
     * @param id id of entity to be removed
     */
    void delete(long id);
    /**
     * Method performs editing logic on entity. All data contains in DTO.
     *
     * @param dto DTO object that is basically original entity's data with updated fields.
     * @return updated data as DTO object
     */
    T update(T dto);
}
