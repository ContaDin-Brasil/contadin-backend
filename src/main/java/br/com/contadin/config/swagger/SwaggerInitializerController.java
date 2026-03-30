package br.com.contadin.config.swagger;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwaggerInitializerController {

    @GetMapping(value = "/swagger-ui/swagger-initializer.js", produces = "application/javascript")
    public ResponseEntity<String> swaggerInitializer() {
        String js = """
                window.onload = function () {
                  "use strict";

                  var SCHEME_NAME = "Bearer";
                  var LOGIN_URL_PATTERN = /\\/auth\\/login/i;

                  function applyToken(ui, token) {
                    if (!ui || !token) return;
                    try {
                      ui.authActions.authorize({
                        Bearer: {
                          name: SCHEME_NAME,
                          schema: { type: "http", in: "header", scheme: "bearer", bearerFormat: "JWT" },
                          value: token
                        }
                      });
                      console.log("[AutoAuth] Token aplicado.");
                    } catch (e) {
                      console.warn("[AutoAuth] Falha ao aplicar token:", e);
                    }
                  }

                  function extractToken(response) {
                    if (!response) return null;
                    var sources = [response.obj, response.data];
                    if (typeof response.text === "string") {
                      try { sources.push(JSON.parse(response.text)); } catch (e) {}
                    }
                    if (typeof response.body === "string") {
                      try { sources.push(JSON.parse(response.body)); } catch (e) {}
                    }
                    for (var i = 0; i < sources.length; i++) {
                      var s = sources[i];
                      if (s && typeof s.accessToken === "string" && s.accessToken.length > 0) {
                        return s.accessToken;
                      }
                    }
                    return null;
                  }

                  window.ui = SwaggerUIBundle({
                    configUrl: "/api-contadin-json/swagger-config",
                    dom_id: "#swagger-ui",
                    deepLinking: true,
                    presets: [SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset],
                    plugins: [SwaggerUIBundle.plugins.DownloadUrl],
                    layout: "StandaloneLayout",
                    persistAuthorization: false,
                    responseInterceptor: function (response) {
                      try {
                        var url = response && response.url ? String(response.url) : "";
                        if (LOGIN_URL_PATTERN.test(url) && response.status >= 200 && response.status < 300) {
                          var token = extractToken(response);
                          if (token) {
                            applyToken(window.ui, token);
                          } else {
                            console.warn("[AutoAuth] Login OK mas accessToken não encontrado:", response);
                          }
                        }
                      } catch (e) {
                        console.error("[AutoAuth] Erro ao processar resposta do login:", e);
                      }
                      return response;
                    }
                  });
                };
                """;

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/javascript"))
                .body(js);
    }
}
