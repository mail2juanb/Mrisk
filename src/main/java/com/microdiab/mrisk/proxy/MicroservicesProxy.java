package com.microdiab.mrisk.proxy;


import com.microdiab.mrisk.bean.NoteBean;
import com.microdiab.mrisk.bean.PatientBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


//@FeignClient(name = "mgateway", url = "localhost:9010", configuration = FeignConfig.class)
@FeignClient(name = "mgateway", url = "localhost:9010")
public interface MicroservicesProxy {


    @GetMapping("/mpatient/patient/{id}")
    PatientBean getPatientById(@PathVariable Long id);


    @GetMapping("mnotes/notes/{patId}")
    List<NoteBean> getNotesByPatId(@PathVariable Long patId);
}
