package com.retail.bo.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.retail.bo.services.RestClient;

@Component
public class OfbizAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private HttpSession httpSession;

    @SuppressWarnings("unchecked")
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String id = authentication.getName();
        String password = authentication.getCredentials().toString();

        RestClient restClient = context.getBean(RestClient.class);
        try {
            restClient.addAuthentication(authentication);

            Map<String, Object> response = restClient.callRetailService("login", true);
            Map<String, Object> responseHeader = (Map)response.get("responseHeader");
            
            if (responseHeader.get("status").toString().equals("LOGGED_IN")) {                
            	RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
                Map<String, Object> party = (Map)response.get("responseBody");
                requestAttributes.setAttribute("party", party, RequestAttributes.SCOPE_SESSION);
            	final List<GrantedAuthority> grantedAuths = new ArrayList<>();
                grantedAuths.add(new SimpleGrantedAuthority("ADMIN"));
                final UserDetails principal = new User(id, password, grantedAuths);
                httpSession.setAttribute("username", principal.getUsername());
                final Authentication auth = new UsernamePasswordAuthenticationToken(principal, password, grantedAuths);
                return auth;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
