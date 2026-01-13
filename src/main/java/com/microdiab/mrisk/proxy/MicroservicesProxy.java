package com.microdiab.mrisk.proxy;


import com.microdiab.mrisk.bean.NoteBean;
import com.microdiab.mrisk.bean.PatientBean;
import com.microdiab.mrisk.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

/**
 * Proxy interface for communicating with microservices mNotes and mPatient in the MicroDiab system.
 * <p>This interface uses Feign to interact with the gateway and retrieve patient and note data.
 * The {@code MicroservicesProxy} interface provides methods to interact with the microservices
 * via the gateway. It is used to fetch patient and note data.</p>
 */
@FeignClient(name = "mgateway", url = "${mgateway.url:http://mgateway:9010}", configuration = FeignConfig.class)
public interface MicroservicesProxy {

    /**
     * Retrieves a patient by their unique identifier.
     *
     * @param id The unique identifier of the patient.
     * @return An {@code Optional} containing the patient data, or empty if not found.
     */
    @GetMapping("/mpatient/patient/{id}")
    Optional<PatientBean> getPatientById(@PathVariable Long id);

    /**
     * Retrieves all notes associated with a patient by their unique identifier.
     *
     * @param patId The unique identifier of the patient.
     * @return A list of notes associated with the patient.
     */
    @GetMapping("mnotes/notes/{patId}")
    List<NoteBean> getNotesByPatId(@PathVariable Long patId);
}
