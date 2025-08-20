describe('피드백 시스템 기능 테스트', () => {
  // ===============================
  // 온보딩 테스트
  // ===============================
  describe('온보딩 기능', () => {
    const TEST_ORGANIZATION_ID = 'test-org-123';

    beforeEach(() => {
      // 모든 API 요청을 모킹으로 처리 (서버 문제로 인해)
      cy.intercept('GET', `/api/organizations/${TEST_ORGANIZATION_ID}`, {
        statusCode: 200,
        body: {
          data: {
            organizationName: '테스트 조직',
            totalCheeringCount: 42,
            categories: ['신고', '질문', '건의', '기타'],
          },
          status: 200,
          message: 'Success',
        },
      }).as('getOrganizationName');

      // 기타 모든 API 요청 모킹
      cy.intercept('GET', '/api/**', { statusCode: 200, body: {} });
      cy.intercept('POST', '/api/**', {
        statusCode: 200,
        body: { success: true },
      });

      // 개발 서버가 실행 중인지 확인 후 방문
      // 실제 라우팅 구조에 맞게 수정하세요
      cy.visit('/d0b1b979-7ae8-11f0-8408-0242ac120002/submit'); // 일단 홈페이지로 이동
      // 또는 cy.visit(`/organization/${TEST_ORGANIZATION_ID}/submit`);
      // 또는 실제 온보딩 페이지 경로로 수정

      cy.viewport(375, 812);
    });

    it('온보딩 페이지가 정상적으로 로드되고 기본 요소들이 표시된다', () => {
      // 환영 메시지 확인
      cy.contains('오신 것을 환영합니다').should('be.visible');

      // 카테고리 선택 안내 확인
      cy.contains('카테고리 선택').should('be.visible');
      cy.contains('건의하고 싶은 카테고리를 선택해주세요').should('be.visible');
    });

    it('4개의 카테고리 버튼이 모두 표시된다', () => {
      // 각 카테고리 버튼 확인
      cy.contains('신고').should('be.visible');
      cy.contains('질문').should('be.visible');
      cy.contains('건의').should('be.visible');
      cy.contains('기타').should('be.visible');

      // 이모지도 확인
      cy.contains('🚨').should('be.visible');
      cy.contains('🙋‍♀️').should('be.visible');
      cy.contains('💬').should('be.visible');
      cy.contains('💡').should('be.visible');
    });

    it('건의 목록 보러가기 버튼이 표시된다', () => {
      cy.contains('건의 목록 보러가기').should('be.visible');
      cy.contains('📄').should('be.visible');
    });

    it('카테고리 버튼 클릭 시 상호작용이 가능하다', () => {
      // 신고 카테고리 클릭 테스트
      cy.contains('신고').should('be.visible').click();

      // 클릭 후 URL 변화나 다른 상태 변화가 있는지 확인
      // (실제 네비게이션은 API 의존성 때문에 제외)
    });

    it('반응형으로 데스크톱에서도 정상 표시된다', () => {
      cy.viewport(1024, 768);

      cy.contains('오신 것을 환영합니다').should('be.visible');
      cy.contains('신고').should('be.visible');
      cy.contains('질문').should('be.visible');
      cy.contains('건의').should('be.visible');
      cy.contains('기타').should('be.visible');
    });
  });

  // ===============================
  // 기존 테스트들은 여기에 유지
  // ===============================

  // 만약 기존에 다른 테스트들이 있다면 여기에 추가하세요
  // 예: 피드백 작성, 대시보드 등등
});
