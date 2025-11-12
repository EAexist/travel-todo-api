package com.matchalab.travel_todo_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.matchalab.travel_todo_api.DTO.ResourceQuotaFetchDTO;
import com.matchalab.travel_todo_api.config.ResourceQuota;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ResourceQuotaMapper {

    public abstract ResourceQuotaFetchDTO mapToResourceQuotaFetchDTO(ResourceQuota resourceQuota);

}