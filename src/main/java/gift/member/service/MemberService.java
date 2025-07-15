package gift.member.service;

import gift.member.domain.Member;
import gift.member.exception.MemberNotFoundException;
import gift.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

  private final MemberRepository memberRepository;

  public MemberService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  public Member findMemberOrThrow(Long memberId) {
    return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
  }
}
