package ru.clevertec.ecl.entity.commitlogentities;

/**
 * Enum containing write operations' titles. Declared constants can be mapped as
 * {@link ru.clevertec.ecl.entity.commitlogentities.CommitLog} field.
 *
 * See also {@link javax.persistence.Enumerated}.
 *
 * @author Olga Mailychko
 *
 */
public enum Action {
    SAVE, UPDATE, DELETE;
}
