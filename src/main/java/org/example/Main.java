package org.example;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class Main {
    record Usuario(String usuario, String contrasena){

    }

    static void main(){
        var app = Javalin.create(config -> {
            //configurando los documentos estaticos.
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/publico";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress=false;
                staticFileConfig.aliasCheck=null;
            });
        });

            //start server on port 7000
            app.start(7000);

            //app
            app.before(/* "/", */ctx -> {

                if(ctx.path().equals("/login.html")
                        || ctx.path().equals("/procesarlogin")
                        || ctx.path().startsWith("/css/")){
                    return;
                }

                System.out.println("Before en el endpoint /");
                Usuario usuario = ctx.sessionAttribute("usuario");
                if (usuario == null) {
                    ctx.redirect("/login.html");
                } else {
                    System.out.println("Usuario en sesion: " + usuario.usuario);
                }
            });

            //end point hola mundo
            app.get("/", ctx -> ctx.redirect("/index.html"));


            app.post("/procesarlogin", ctx -> {
                String usuario = ctx.formParam("nombre");
                String contrasena = ctx.formParam("contrasena");
                //
                if (usuario.equals("admin") && contrasena.equals("1234")) {
                    ctx.sessionAttribute("usuario", new Usuario(usuario, contrasena));
                }//
                ctx.redirect("/");
            });

            app.get("/otro-endpoint", ctx -> {
                ctx.result("Hello desde otro endpoint");
            });

            app.get("/logout", ctx -> {
                ctx.req().getSession().invalidate();
                ctx.redirect("/login.html");
            });

    }
}
