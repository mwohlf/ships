package net.wohlfart.ships.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * if the user clicks reload in the browser we need to deliver the index.html file,
 * however we don't want this to happen for *.css, *.js or the api, so make some assumptions...
 */
@Slf4j
@Component
public class AngularRoutesFilter implements Filter {

    static final String API_PATH = "/api";
    static final String SWAGGER_CONFIG_PATH = "/v3/api-docs";
    static final String SWAGGER_UI_PATH = "/swagger-ui";

    static final String INDEX_HTML = "/index.html";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String uri = ((HttpServletRequest) request).getRequestURI();
        if (isAngularRoute(uri)) {
            log.debug("re-routing: '{}' to the {} resource", uri, INDEX_HTML);
            request.getRequestDispatcher(INDEX_HTML).forward(request, response);
        } else {
            log.debug("serving: '{}'", uri);
            chain.doFilter(request, response);
        }
    }

    private boolean isAngularRoute(@NonNull String path) {
        return
            !path.contains(".")  // this indicates a html,jpg,js file we don't want to reroute that
                && !path.startsWith(API_PATH) // don't want to reroute api calls
                && !path.startsWith(SWAGGER_CONFIG_PATH)
                && !path.startsWith(SWAGGER_UI_PATH)

            ;
    }

}
