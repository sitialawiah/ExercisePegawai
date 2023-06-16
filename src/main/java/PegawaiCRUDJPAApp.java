import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import id.co.indivara.jdt12.entity.Pegawai;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PegawaiCRUDJPAApp {
    private Pegawai hibernate;
    public PegawaiCRUDJPAApp (Pegawai hibernate){this.hibernate=hibernate;}

    public static Session initHibernate (Class namaClassEntity){
        Configuration config=new Configuration();
        config.configure("hibernate.cfg.xml");
        config.addAnnotatedClass(namaClassEntity);
        SessionFactory sf=config.buildSessionFactory();
        Session session=sf.openSession();
        return session;
    }

    public static void main(String[] args) throws Exception{
        int nip;
        Scanner s=new Scanner(System.in);
        Session session=initHibernate(Pegawai.class);
        while (true){
            session.getTransaction();
            System.out.println("=====MENU=====");
            System.out.println("1. Tampilkan Semua Pegawai"); //outputnya file json
            System.out.println("2. Input Data Pegawai"); //input dari file json , kaya latihan dao tester
            System.out.println("3. Cari Data Pegawai"); //outputnya file json
            System.out.println("4. Update Data Pegawai");
            System.out.println("5. Input Banyak Pegawai");
            System.out.println("6. Hapus Data Pegawai");
            System.out.println("7. Keluar");
            System.out.print("Pilihan Anda [1/2/3/4/5/6/7]?");
            String pilihan=s.nextLine();
            if (pilihan.equals("1")){ //tampilkan
                List<Pegawai> pegawais=session.createQuery("from Pegawai").list();
                    ObjectMapper mapper=new ObjectMapper();
                    ObjectWriter ow=mapper.writer(new DefaultPrettyPrinter());
                    ow.writeValue(new File("d:/siti.json"),pegawais);

            } else if (pilihan.equals("2")) { //input
                session.beginTransaction();

                ObjectMapper mapper=new ObjectMapper();
                Pegawai pgw=mapper.readValue(new File("d:/pegawai.json"),Pegawai.class);

                session.save(pgw);
                session.getTransaction().commit();
                session.clear();
                System.out.println("===Data Pegawai Berhasil Diinput===");

            } else if (pilihan.equals("3")) { //cari
                session.beginTransaction();

                System.out.println("Masukkan NIP yang akan dicari :");
                System.out.println("NIP :");
                nip = Integer.parseInt(s.nextLine());
                Pegawai pgw=session.load(Pegawai.class,nip);
                ObjectMapper mapper = new ObjectMapper();
                mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
                mapper.writeValue(new File("d:/caridatapegawai.json"), pgw);

            } else if (pilihan.equals("4")) { //update
                session.beginTransaction();

                ObjectMapper mapper=new ObjectMapper();
                Pegawai pgw=mapper.readValue(new File("d:/caridatapegawai.json"),Pegawai.class);

                session.update(pgw);
                session.getTransaction().commit();
                session.clear();
                System.out.println("===Data Pegawai berhasil diupdate===");

            } else if (pilihan.equals("5")) { //input banyak
                ObjectMapper mapper=new ObjectMapper();
                ArrayList<Pegawai> pgwList=mapper.readValue(
                        new File("d:/daftarpegawai.json"),
                        new TypeReference<ArrayList<Pegawai>>(){}
                );
                for (Pegawai pgw:pgwList) {
                    session.beginTransaction();
                    session.save(pgw);
                    session.getTransaction().commit();
                    session.clear();
                }
                System.out.println("===Data Pegawai Berhasil Diinput===");

            } else if (pilihan.equals("6")) { //hapus
                ObjectMapper mapper=new ObjectMapper();
                Pegawai pgw=mapper.readValue(new File("d:/pegawai.json"),Pegawai.class);

                session.beginTransaction();
                session.delete(pgw);
                session.getTransaction().commit();
                System.out.println("===Data Pegawai berhasil didelete===");

            } else if (pilihan.equals("7")) { //keluar
                System.exit(0);
            }
        }
    }
}