package business.client;

import business.ASException;
import business.client.as.ASClient;
import business.client.factory.ASClientFactory;
import business.rental.TRental;
import business.rental.as.ASRental;
import business.rental.factory.ASRentalFactory;
import business.vehicle.TVehicle;
import business.vehicle.as.ASVehicle;
import business.vehicle.factory.ASVehicleFactory;
import integration.client.dao.DAOClient;
import integration.client.factory.DAOClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ASClientTest {
    private static Date dFrom = new Date(1540373530000L);
    private static Date dTo = new Date(1543051930000L);
    private static ASClient as = ASClientFactory.getInstance().generateASClient();
    private static TClient tc = new TClient(null,"00000000X",0,false);
    private static ASVehicle asV = ASVehicleFactory.getInstance().generateASVehicle();
    private static TVehicle tv = new TVehicle(null,"Audi",6000,0,
            false,null,false,"car");
    private static ASRental asR = ASRentalFactory.getInstance().generateASRental();
    private static TRental tr = new TRental(null,null,false,10,null,dFrom,dTo);

    @BeforeEach
    private void setUp() throws Exception{
        DAOClient dao = DAOClientFactory.getInstance().generateDAOClient();
        dao.deleteAll();

    }

    //Create method
    @Test
    public void createClientSuccessful(){
       assertTrue(as.create(tc)>0);
    }

    @Test (expected = IncorrectInputException.class)
    public void createClientIncorrectInput0(){
        tc.setIdCardNumber("0000X");// must have 8 numbers and 1 letter at the end
        as.create(tc);
    }

    @Test (expected = IncorrectInputException.class)
    public void createClientIncorrectInput1(){
        tc.setIdCardNumber("00000000");
        // must have 8 numbers and 1 letter at the end
        as.create(tc);
    }

    @Test (expected = IncorrectInputException.class)
    public void createClientIncorrectInput2(){
        tc.setNumRentals(-1); //rentals_number must be >= 0
        as.create(tc);
    }


    //Drop method
    @Test
    public void dropSuccessful(){
        Integer idV = asV.create(tv);
        Integer id = as.create(tc);
        tr.setIdClient(id);
        tr.setIdVehicle(idV);
        asR.create(tr);

        as.drop(id);
        assertTrue(as.showAll().isEmpty());
        assertTrue(asR.showAll().isEmpty());
    }

    @Test (expected = IncorrectInputException.class)
    public void dropClientIncorrectInput0(){
        as.drop(0); //id must be > 0
    }

    @Test (expected = ASException.class)
    public void dropClientNotExists(){
        as.drop(50);
    }

    @Test (expected = ASException.class)
    public void dropClientWithActiveRentals(){
        Integer idC = as.create(tc);
        Integer idV = asV.create(tv);

        tr.setIdClient(idC);
        tr.setIdVehicle(idV);
        asR.create(tr);

        as.drop(idC);
    }


    //Show method

    @Test
    public void showClientSuccessful(){
        Integer idC = as.create(tc);

        TClient out = as.show(idC);

        assertEquals(out.getId(),idC);
        assertEquals(out.getIdCardNumber(),tc.getIdCardNumber());
        assertEquals(out.getNumRentals(),tc.getNumRentals());
        assertEquals(out.isActive(),tc.isActive());
    }

    @Test (expected = IncorrectInputException.class)
    public void showClientIncorrectInputID1(){
        as.show(0); //id must be > 0
    }

    @Test (expected = IncorrectInputException.class)
    public void showClientIncorrectInputID2(){
        as.show(-1); //id must be > 0
    }

    @Test (expected = ASException.class)
    public void showClientNotExists(){
        as.show(1);
    }

    //showAll method
    @Test
    public void showAllClientsSuccessful() {
        Integer idC = as.create(tc);
        TClient tc2 = new TClient(null, "11111111X", 0, false);
        as.create(tc2);

        Collection<TClient> collec = as.showAll();
        for (TClient tmp : collec) {
            if (tmp.getId().equals(idC))
                assertTrue(checkTransferValues(tmp, tc.getIdCardNumber(), tc.getNumRentals()));
           else
                assertTrue(checkTransferValues(tmp, tc2.getIdCardNumber(), tc2.getNumRentals()));
        }
    }

    @Test
    public void showAllClientsSuccessful2(){
        Collection<TClient> c = as.showAll();
        assertTrue(c.isEmpty());
    }
    private boolean checkTransferValues(TClient tc,String id_card_number,Integer rentals_number){
        return  tc.getIdCardNumber().equals(id_card_number) &&
                tc.getNumRentals().equals(rentals_number) && tc.isActive();
    }
    //showAllWithMoreNrentals method

    @Test
    public void showAllClientsWithMoreNrentalsSuccessful(){
        final Integer N = 2;
        tc.setNumRentals(3);
        as.create(tc);
        TClient tc2 = new TClient(null, "11111111X", 2, false);
        Integer idC2 = as.create(tc2);
        TClient tc3 = new TClient(null, "11121111Y", 0, false);
        Integer idC3 = as.create(tc3);

        Collection<TClient> collec = as.showAllWithMoreThanNRentals(N);
        for(TClient tmp : collec){
            assertTrue(tmp.getNumRentals() > N);
            assertNotEquals(tmp.getId(),idC2);
            assertNotEquals(tmp.getId(),idC3);
        }
    }
    //Update method
    @Test
    public void updateClientSuccessful(){
        Integer idC = as.create(tc);

        TClient updtClient = new TClient(idC,"11111111X",1,true);
        Integer idOut = as.update(updtClient);

        TClient out = as.show(idOut);

        assertEquals(out.getId(),idC);
        assertEquals(out.getIdCardNumber(),updtClient.getIdCardNumber());
        assertEquals(out.getNumRentals(),updtClient.getNumRentals());
        assertEquals(out.isActive(),updtClient.isActive());
    }

    @Test (expected = IncorrectInputException.class)
    public void updateClientIncorrectInputID(){
        //id can't be null and must be > 0
        tc.setId(null);
        as.update(tc);
    }

    @Test (expected = IncorrectInputException.class)
    public void updateClientIncorrectInputID_card_number1(){
        //id_card_number must have 8 numbers and 1 letter at the end
       tc.setIdCardNumber("1111X");
        as.update(tc);
    }

    @Test (expected = IncorrectInputException.class)
    public void updateClientIncorrectInputID_card_number2(){
        //id_card_number must have 8 numbers and 1 letter at the end
        tc.setIdCardNumber("11111111");
        as.update(tc);
    }

    @Test (expected = IncorrectInputException.class)
    public void updateClientIncorrectInputRentals_number(){
        //rentals number must be >= 0
        tc.setNumRentals(-1);
        as.update(tc);
    }

    @Test (expected = ASException.class)
    public void updateClientIncorrectInputActive(){
        //active must be true
        tc.setActive(false);
        as.update(tc);
    }

    @Test (expected = IncorrectInputException.class)
    public void updateClientNotExists(){
        tc.setId(1);
        as.update(tc);
    }
}
