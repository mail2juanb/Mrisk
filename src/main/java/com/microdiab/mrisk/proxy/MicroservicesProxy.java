package com.microdiab.mrisk.proxy;


import com.microdiab.mrisk.bean.NoteBean;
import com.microdiab.mrisk.bean.PatientBean;
import com.microdiab.mrisk.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;


@FeignClient(name = "mgateway", url = "localhost:9010", configuration = FeignConfig.class)
public interface MicroservicesProxy {


    @GetMapping("/mpatient/patient/{id}")
    Optional<PatientBean> getPatientById(@PathVariable Long id);


    @GetMapping("mnotes/notes/{patId}")
    List<NoteBean> getNotesByPatId(@PathVariable Long patId);
}
