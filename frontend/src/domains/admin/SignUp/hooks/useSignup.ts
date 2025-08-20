import { AdminAuthResponse, postAdminSignup } from '@/apis/admin.api';
import { ApiError } from '@/apis/apiClient';
import { ADMIN_BASE, ROUTES } from '@/constants/routes';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { setLocalStorage } from '@/utils/localStorage';
import { useMutation } from '@tanstack/react-query';
import { FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';

interface UseSignupProps {
  confirmPasswordErrors: string;
  errors: {
    [key: string]: string;
  };
  signUpValue: {
    name: string;
    id: string;
    password: string;
  };
  setToast: (message: string | null) => void;
}

export default function useSignup({
  confirmPasswordErrors,
  errors,
  signUpValue,
  setToast,
}: UseSignupProps) {
  const navigate = useNavigate();
  const { showErrorModal } = useErrorModalContext();
  const { handleApiError } = useApiErrorHandler();

  const mutate = useMutation({
    mutationFn: () =>
      postAdminSignup({
        loginId: signUpValue.id,
        password: signUpValue.password,
        adminName: signUpValue.name,
      }),
    onError: (error) => {
      showErrorModal(
        '회원가입에 실패했습니다. 다시 시도해 주세요.',
        '회원가입 실패'
      );
      handleApiError(error as ApiError);
    },
    onSuccess: (response: AdminAuthResponse) => {
      setLocalStorage('auth', response.data);
      navigate(ADMIN_BASE + ROUTES.ADMIN_HOME);
    },
  });

  const handleSignUp = (event: FormEvent) => {
    event.preventDefault();
    const isValid = Object.values(errors).every((error) => !error);
    const isConfirmPasswordValid =
      !confirmPasswordErrors && signUpValue.password !== '';

    if (!isValid || !isConfirmPasswordValid) {
      setToast('입력하신 정보를 다시 확인해주세요.');
      return;
    }

    mutate.mutate();
  };

  return {
    handleSignUp,
    isLoading: mutate.isPending,
  };
}
