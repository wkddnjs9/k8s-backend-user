package com.welab.k8s_backend_user.service;

import com.welab.k8s_backend_user.domain.SiteUser;
import com.welab.k8s_backend_user.domain.dto.SiteUserRegisterDto;
import com.welab.k8s_backend_user.domain.event.SiteUserInfoEvent;
import com.welab.k8s_backend_user.domain.repository.SiteUserRepository;
import com.welab.k8s_backend_user.event.producer.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사이트 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SiteUserService {
    private final SiteUserRepository siteUserRepository;
    private final KafkaMessageProducer kafkaMessageProducer;

    /**
     * 새로운 사용자를 등록합니다.
     * {@link SiteUserRegisterDto}로부터 {@link SiteUser} 엔티티를 생성하여 저장합니다.
     * 이 메서드는 트랜잭션 내에서 실행됩니다.
     *
     * @param registerDto 사용자 등록 정보를 담고 있는 DTO
     */
    @Transactional
    public void registerUser(SiteUserRegisterDto registerDto) {
        SiteUser siteUser = registerDto.toEntity();
        siteUserRepository.save(siteUser);
        SiteUserInfoEvent event = SiteUserInfoEvent.fromEntity("Create", siteUser);
        kafkaMessageProducer.send(SiteUserInfoEvent.Topic, event);
    }
}
