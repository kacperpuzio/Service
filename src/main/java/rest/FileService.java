package rest;
import hibernate.HibernateUtil;
import hibernate.dto.ImageWrapper;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Path("/images")
public class FileService {

    List<ImageWrapper> list;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImages() {
        list = new ArrayList<>();
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            list = session.createQuery("from ImageWrapper", ImageWrapper.class).list();
            GenericEntity entity = new GenericEntity<List<ImageWrapper>>(list){};
            transaction.commit();
            return Response.ok(entity).build();
        }
        catch(Exception e){
            if (transaction != null) {
                transaction.rollback();
            }
            return Response.status(500).entity("Błąd poczas ładowania pliku: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("getNames")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNames() {
        list = new ArrayList<>();
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            list = session.createQuery("SELECT NEW ImageWrapper(i.id, i.imageName) FROM  ImageWrapper i ", ImageWrapper.class).list();

            GenericEntity entity = new GenericEntity<List<ImageWrapper>>(list){};
            transaction.commit();
            return Response.ok(entity).build();
        }
        catch(Exception e){
            if (transaction != null) {
                transaction.rollback();
            }
            return Response.status(500).entity("Błąd podczas pobierania id i nazw: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") int id) {
        ImageWrapper image = new ImageWrapper();
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
//            image = session.createQuery("SELECT NEW ImageWrapper(i.id, i.imageName, i.imageData) FROM  ImageWrapper i WHERE i.id = id", ImageWrapper.class).getSingleResult();
            image = session.get(ImageWrapper.class, id);

            GenericEntity entity = new GenericEntity<ImageWrapper>(image){};

            transaction.commit();
            return Response.ok(entity).build();
        }
        catch(Exception e){
            if (transaction != null) {
                transaction.rollback();
            }
            return Response.status(500).entity("Błąd podczas pobierania ikony: " + e.getMessage()).build();
        }
    }


    @DELETE
    @Path("{id}")
    public Response deleteImage(@PathParam("id") int idDel){
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
//            session.createQuery("DELETE FROM ImageWrapper i WHERE i.id = id", ImageWrapper.class);
            ImageWrapper image = session.get(ImageWrapper.class, idDel);
//            Query q = session.createQuery("delete ImageWrapper i where i.id = idDel");
//            q.executeUpdate();
            session.delete(image);
            transaction.commit();
            return Response.status(204).build();
        }
        catch(Exception e){
            if (transaction != null) {
                transaction.rollback();
            }
            return Response.status(500).entity("Błąd podczas usuwania ikony " + e.getMessage()).build();
        }
    };


    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) {
        byte[] imageData = new byte[65535];
        try {
            imageData = toByteArray(uploadedInputStream);
        }
        catch (FileNotFoundException e) {
            System.out.println("Failed to open file stream:" + e.getMessage());
        }
        catch (IOException e) {
            System.out.println("Failed to read from file stream:" + e.getMessage());
        }
        finally{
            if(uploadedInputStream != null){
                try {
                    uploadedInputStream.close();

                } catch (IOException e) {
                    System.out.println("Failed to close InputStream: " + e.getMessage());
                }
            }
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // save the img object
            ImageWrapper image = new ImageWrapper();
            image.setImageName(fileDetail.getName());
            image.setImageData(imageData);
            session.save(image);
            // commit transaction
            transaction.commit();
            return Response.status(200).entity("Załadowano").build();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(500).entity("Błąd poczas ładowania pliku").build();
        }
    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        // read bytes from the input stream and store them in buffer
        while ((len = in.read(buffer)) != -1) {
            // write bytes from the buffer into output stream
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }
}
