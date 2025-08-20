import { AdminAuthResponse, postAdminLogin } from '@/apis/admin.api';
import { ApiError } from '@/apis/apiClient';
import { ADMIN_BASE, ROUTES } from '@/constants/routes';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useApiErrorHandler } from '@/hooks/useApiErrorHandler';
import { setLocalStorage } from '@/utils/localStorage';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

interface UseLoginProps {
  loginValue: {
    id: string;
    password: string;
  };
}

export default function useLogin({ loginValue }: UseLoginProps) {
  const navigate = useNavigate();
  const { showErrorModal } = useErrorModalContext();
  const { handleApiError } = useApiErrorHandler();

  const mutate = useMutation({
    mutationFn: () =>
      postAdminLogin({
        loginId: loginValue.id,
        password: loginValue.password,
      }),
    onError: (error) => {
      showErrorModal(
        '로그인에 실패했습니다. 아이디와 비밀번호를 확인해 주세요.',
        '로그인 실패'
      );
      handleApiError(error as ApiError);
    },
    onSuccess: (response: AdminAuthResponse) => {
      setLocalStorage('auth', response.data);
      navigate(ADMIN_BASE + ROUTES.ADMIN_HOME);
    },
  });

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    setLocalStorage('auth', null);
    mutate.mutateAsync();
  };

  return {
    handleSubmit,
  };
}
