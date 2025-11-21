package com.matchalab.travel_todo_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.matchalab.travel_todo_api.DTO.ResourceQuotaFetchDTO;
import com.matchalab.travel_todo_api.config.ResourceQuota;
import com.matchalab.travel_todo_api.mapper.ResourceQuotaMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class ResourceQuotaController {

    @Autowired
    ResourceQuota resourceQuota;

    @Autowired
    ResourceQuotaMapper resourceQuotaMapper;

    /**
     * Provide the resource quota.
     */
    @GetMapping("/resourceQuota")
    public ResponseEntity<ResourceQuotaFetchDTO> getResourceQuota() {
        try {
            return ResponseEntity.ok().body(resourceQuotaMapper.mapToResourceQuotaFetchDTO(resourceQuota));
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }

}
