export interface OrganizationResponse {
  data: {
    organizationName: string;
    totalCheeringCount: number;
    categories: string[];
  };
}

export type CategoryType = '신고' | '질문' | '건의' | '기타';
