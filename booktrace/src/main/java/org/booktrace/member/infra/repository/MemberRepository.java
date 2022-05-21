package org.booktrace.member.infra.repository;

import org.booktrace.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true) // ReadOnly
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email); // 이메일 중복 확인

    boolean existsByNickname(String nickname); // 닉네임 중복 확인
}
