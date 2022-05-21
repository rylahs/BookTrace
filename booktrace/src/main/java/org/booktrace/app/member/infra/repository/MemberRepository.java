package org.booktrace.app.member.infra.repository;

import org.booktrace.app.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true) // ReadOnly
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email); // 이메일 중복 확인

    boolean existsByNickname(String nickname); // 닉네임 중복 확인

    Member findByEmail(String email); // 이메일로 회원 정보 조회
}
