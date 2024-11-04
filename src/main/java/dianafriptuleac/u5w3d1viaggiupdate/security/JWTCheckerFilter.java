package dianafriptuleac.u5w3d1viaggiupdate.security;

import dianafriptuleac.u5w3d1viaggiupdate.tools.JWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//Component obbligatorio- x poter utilizzare la classe nella cattena filtri
@Component
public class JWTCheckerFilter extends OncePerRequestFilter {

    @Autowired
    private JWT jwt;

    //Metodo richiamato ad ogni richiesta - controlla il token
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Verifico se nella richiesta è presente l'Authorization Header,
        // e se è ben formato ("Bearer josdjojosdj..."), altimenti - 401
        String authorizedHeader = request.getHeader("Authorization");
        if (authorizedHeader == null || !authorizedHeader.startsWith("Bearer")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Inserire token nell'Authorization Header e nel formato corretto!");
            return;
        }
        //2.Estrago il token dal header
        String accessToken = authorizedHeader.substring(7); // 7-nr. caratteri prima del token (Bearer )

        //3.Verifico il token con il metodo del jwt.verifyToken(accessToken) che si trova nel tools.
        try {
            jwt.verifyToken(accessToken);

            //4. Se e tutto ok - passo la richiesta al prossiimo filtro con filterChain.
            //con doFilter(request, response) riichiamo il prossimo filtro o controller della catena
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            //5. se c'e qualcosa che non va con il token ci da un 401.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token non valido o scaduto. Effettuare un nuovo login.");

        }
    }

    // Disabilitare il filtro per tutte le richieste al controller Auth, quindi tutte le richieste che avranno come URL /auth/** non dovranno
    // avere il controllo del token
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return new AntPathMatcher().match("/auth/**", request.getServletPath());
    }
}


