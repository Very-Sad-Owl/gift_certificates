package ru.clevertec.ecl.service;

import ru.clevertec.ecl.dto.CertificateDto;

public interface CertificateService extends CRUDService<CertificateDto> {
    long getSequenceNextVal();
    long getSequenceCurrVal();
    void updateSequence(long val);
}
