package feedzupzup.backend.auth.presentation.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InternalFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private InternalFilter internalFilter;

    @Test
    @DisplayName("localhost(IPv4)에서 internal API 요청 시 필터를 통과한다")
    void localhost_ipv4_passes() throws Exception {
        // Given
        given(request.getRequestURI()).willReturn("/internal/sse/disconnect");
        given(request.getRemoteAddr()).willReturn("127.0.0.1");

        // When
        internalFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("localhost(IPv6)에서 internal API 요청 시 필터를 통과한다")
    void localhost_ipv6_passes() throws Exception {
        // Given
        given(request.getRequestURI()).willReturn("/internal/sse/disconnect");
        given(request.getRemoteAddr()).willReturn("0:0:0:0:0:0:0:1");

        // When
        internalFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("외부 IP에서 internal API 요청 시 403을 반환한다")
    void external_ip_returns_403() throws Exception {
        // Given
        given(request.getRequestURI()).willReturn("/internal/sse/disconnect");
        given(request.getRemoteAddr()).willReturn("192.168.1.100");

        // When
        internalFilter.doFilterInternal(request, response, filterChain);

        // Then
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(response).sendError(statusCaptor.capture());
        assertThat(statusCaptor.getValue()).isEqualTo(403);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("internal 경로가 아닌 요청은 IP에 관계없이 필터를 통과한다")
    void non_internal_path_passes() throws Exception {
        // Given
        given(request.getRequestURI()).willReturn("/admin/organizations");

        // When
        internalFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }
}