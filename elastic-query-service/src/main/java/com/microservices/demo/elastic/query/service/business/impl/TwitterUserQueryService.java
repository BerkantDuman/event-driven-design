package com.microservices.demo.elastic.query.service.business.impl;

import com.microservices.demo.elastic.query.service.business.UserQueryService;
import com.microservices.demo.elastic.query.service.dataaccess.entity.UserPermission;
import com.microservices.demo.elastic.query.service.dataaccess.repository.UserPermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TwitterUserQueryService implements UserQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterUserQueryService.class);

    private final UserPermissionRepository userPermissionRepository;

    public TwitterUserQueryService(UserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }


    @Override
    public Optional<List<UserPermission>> findAllPermissionsByUsername(String username) {
        LOG.info("Finding permissions by username {}", username);
        return userPermissionRepository.findPermissionsByUsername(username);
    }
}
