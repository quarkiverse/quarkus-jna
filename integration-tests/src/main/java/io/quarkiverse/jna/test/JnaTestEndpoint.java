package io.quarkiverse.jna.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

@Path("jna")
public class JnaTestEndpoint {

    // This is the standard, stable way of mapping, which supports extensive
    // customization and mapping of Java to native types.
    public interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary) Native.load((Platform.isWindows() ? "msvcrt" : "c"),
                CLibrary.class);

        int atoi(String value);
    }

    @GET
    @Path("{id}")
    public int atoi(@PathParam String id) {
        return CLibrary.INSTANCE.atoi(id) + 1;
    }
}
