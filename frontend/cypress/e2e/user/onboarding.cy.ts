/// <reference types="cypress" />

describe('온보딩 테스트', () => {
  const TEST_ORGANIZATION_ID = 'test-org-123';

  beforeEach(() => {
    // API 모킹
    cy.intercept('GET', `/api/organizations/${TEST_ORGANIZATION_ID}/name`, {
      fixture: 'organization.json',
    }).as('getOrganizationName');

    // 온보딩 페이지 방문
    cy.visit(`/${TEST_ORGANIZATION_ID}/submit`);
    cy.viewport(375, 812); // 모바일 뷰포트
  });

  describe('초기 렌더링', () => {
    it('온보딩 페이지가 정상적으로 로드된다', () => {
      cy.wait('@getOrganizationName');

      // 환영 메시지 확인
      cy.contains('오신 것을 환영합니다').should('be.visible');

      // 조직명이 표시되는지 확인
      cy.contains('테스트 조직').should('be.visible');

      // 카테고리 선택 안내 텍스트 확인
      cy.contains('카테고리 선택').should('be.visible');
      cy.contains('건의하고 싶은 카테고리를 선택해주세요').should('be.visible');
    });

    it('4개의 카테고리 버튼이 모두 표시된다', () => {
      cy.wait('@getOrganizationName');

      // 각 카테고리 버튼 확인
      cy.contains('🚨').should('be.visible');
      cy.contains('신고').should('be.visible');

      cy.contains('🙋‍♀️').should('be.visible');
      cy.contains('질문').should('be.visible');

      cy.contains('💬').should('be.visible');
      cy.contains('건의').should('be.visible');

      cy.contains('💡').should('be.visible');
      cy.contains('기타').should('be.visible');
    });

    it('건의 목록 보러가기 버튼이 표시된다', () => {
      cy.wait('@getOrganizationName');

      cy.contains('📄').should('be.visible');
      cy.contains('건의 목록 보러가기').should('be.visible');
    });
  });

  describe('카테고리 선택', () => {
    it('신고 카테고리를 선택하면 피드백 페이지로 이동한다', () => {
      cy.wait('@getOrganizationName');

      cy.contains('신고').click();

      // URL 변경 확인 (submit 페이지 내에서 feedback으로 이동한다고 가정)
      cy.url().should('include', `/${TEST_ORGANIZATION_ID}/submit/feedback`);
    });

    it('질문 카테고리를 선택하면 피드백 페이지로 이동한다', () => {
      cy.wait('@getOrganizationName');

      cy.contains('질문').click();

      cy.url().should('include', `/${TEST_ORGANIZATION_ID}/submit/feedback`);
    });

    it('건의 카테고리를 선택하면 피드백 페이지로 이동한다', () => {
      cy.wait('@getOrganizationName');

      cy.contains('건의').click();

      cy.url().should('include', `/${TEST_ORGANIZATION_ID}/submit/feedback`);
    });

    it('기타 카테고리를 선택하면 피드백 페이지로 이동한다', () => {
      cy.wait('@getOrganizationName');

      cy.contains('기타').click();

      cy.url().should('include', `/${TEST_ORGANIZATION_ID}/submit/feedback`);
    });
  });

  describe('건의 목록 보러가기', () => {
    it('건의 목록 보러가기 버튼을 클릭하면 대시보드로 이동한다', () => {
      cy.wait('@getOrganizationName');

      cy.contains('건의 목록 보러가기').click();

      cy.url().should('include', `/${TEST_ORGANIZATION_ID}/dashboard`);
    });
  });

  describe('에러 처리', () => {
    it('조직 정보 로딩 실패 시 기본값을 표시한다', () => {
      // 에러 응답 모킹
      cy.intercept('GET', `/api/organizations/${TEST_ORGANIZATION_ID}/name`, {
        statusCode: 500,
      }).as('getOrganizationNameError');

      cy.visit(`/${TEST_ORGANIZATION_ID}/submit`);
      cy.wait('@getOrganizationNameError');

      // 기본 조직명 표시 확인
      cy.contains('피드줍줍').should('be.visible');
    });
  });

  describe('반응형 테스트', () => {
    it('데스크톱 뷰에서도 정상적으로 표시된다', () => {
      cy.viewport(1024, 768);
      cy.wait('@getOrganizationName');

      cy.contains('오신 것을 환영합니다').should('be.visible');
      cy.contains('신고').should('be.visible');
      cy.contains('질문').should('be.visible');
      cy.contains('건의').should('be.visible');
      cy.contains('기타').should('be.visible');
    });
  });
});
