package zerobase.group2.cookingRecipe.Security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.ObjectUtils;

public class JsonUsernamePasswordAuthFilter extends AbstractAuthenticationProcessingFilter {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    protected JsonUsernamePasswordAuthFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response)
        throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                "Authentication method not supported: " + request.getMethod());
        }

        JSONObject jsonObject = parseToJsonObject(request);

        String username = (String) jsonObject.get(USERNAME);
        username = ObjectUtils.isEmpty(username) ? "" : username.trim();
        String password = (String) jsonObject.get(PASSWORD);
        password = ObjectUtils.isEmpty(password) ? "" : password.trim();

        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(
            username, password);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private JSONObject parseToJsonObject(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String bodyJson = stringBuilder.toString();
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(bodyJson);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
