package business.city;

import business.ASException;
import business.city.as.ASCity;
import business.city.factory.ASCityFactory;
import business.client.TClient;
import business.client.as.ASClient;
import business.client.factory.ASClientFactory;
import business.rental.TRental;
import business.rental.as.ASRental;
import business.rental.factory.ASRentalFactory;
import business.vehicle.TVehicle;
import business.vehicle.as.ASVehicle;
import business.vehicle.factory.ASVehicleFactory;
import integration.city.dao.DAOCity;
import integration.city.factory.DAOCityFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


public class ASCityTest {

    private static ASCity 	as = ASCityFactory.getInstance().generateASCity();
	private static TCity tc = new TCity(null,"Madrid",false);
    private static ASVehicle 	asV = ASVehicleFactory.getInstance().generateASVehicle();
    private static TVehicle tv = new TVehicle(null,"Audi",6000,0,
            false,null,false,"car");
    private static ASClient asC = ASClientFactory.getInstance().generateASClient();
    private static TClient tclient = new TClient(null,"00000000X",0,false);
    private static ASRental asR = ASRentalFactory.getInstance().generateASRental();
    private static TRental tr = new TRental(null,null,false,10,null,null,null);






    @BeforeEach
	private void setUp() throws Exception {
		DAOCity dao = DAOCityFactory.getInstance().generateDAOCity();
		dao.deleteAll();
	}

	//create method tests
	@Test
	public void createCitySuccessful(){
		assertTrue(as.create(tc) > 0);
	}

	@Test (expected = IncorrectInputException.class)
	public void createCityIncorrectInput1(){
	    tc.setId(-1); //id it's generated by database,
                    // so in create method must be null as input
		as.create(tc);
	}

	@Test (expected = IncorrectInputException.class)
	public void createCityIncorrectInput2(){
		tc.setActive(true); //active must be false as input
		as.create(tc);
	}

	//EN FUNCION DE SI HAY CAMBIOS O NO EN LOS CASOS DE USO QUITARLO
	@Test (expected = ASException.class)
	public void createCityAlreadyExists(){
	    as.create(tc);
		//city already exists
		as.create(tc);
	}

	//drop method tests

	@Test
	public void dropCitySuccessful(){
        Integer idV = asV.create(tv);
		Integer id = as.create(tc);
		Integer idC = asC.create(tclient);
		tr.setIdVehicle(idV);
		tr.setIdClient(idC);
		asR.create(tr);

		assertTrue(as.showAll().isEmpty());
		assertTrue(as.showClientsByCity(id).isEmpty());
		assertTrue(asV.showAll().isEmpty());
		assertTrue(asR.showAll().isEmpty());
	}


	@Test (expected = IncorrectInputException.class)
	public void dropCityIncorrectInput1(){
		Integer id = 0; //id must be > 0
		as.drop(id);
	}

	@Test (expected = IncorrectInputException.class)
	public void dropCityIncorrectInput2(){
	    Integer id = -1; //id must be > 0
		as.drop(id);
	}

	@Test (expected = ASException.class)
	public void dropCityNotExists(){
		Integer id = 1;
		as.drop(id);
	}

	@Test (expected = ASException.class)
	public void dropCityAlreadyInactive(){
		Integer id = as.create(tc);
		as.drop(id);

		as.drop(id);
	}

	@Test (expected = ASException.class)
	public void dropCityWithActiveVehicles(){
		Integer id = as.create(tc);


		tv.setId(id);
		asV.create(tv);

		as.drop(id);
	}


	//show method tests
	@Test
	public void showCitySuccessful(){
		Integer id = as.create(tc);

		TCity tmp = as.show(id);

		assertEquals(tmp.getId(),id);
		assertEquals(tmp.getName(),tc.getName());
		assertTrue(tmp.isActive());
	}

	@Test (expected = IncorrectInputException.class)
	public void showCityIncorrectInput1(){
		Integer id = 0; //id must be > 0
		as.show(id);
	}

	@Test (expected = IncorrectInputException.class)
	public void showCityIncorrectInput2(){
		Integer id = -1; //id must be > 0
		as.show(id);
	}

	@Test (expected = ASException.class)
	public void showCityNotExists(){
		Integer id = 1;
		as.show(id);
	}

	//showAll method tests
	@Test
	public void showAllCitySuccessful1(){
		Integer idMad = as.create(tc);
		tc.setId(idMad);

		tc.setName("Barcelona");
		Integer idBcn = as.create(tc);
		tc.setId(idBcn);

		Collection<TCity> c = as.showAll();

		for(TCity tmp : c){
		    if(tmp.getId().equals(idMad))
			   assertTrue(checkTransferValues(tmp,"Madrid"));
			else
			    assertTrue(checkTransferValues(tmp,"Barcelona"));

		}
	}

    private boolean checkTransferValues(TCity out, String city) {
	    return out.getName().equals(city) && out.isActive();
    }

    @Test
	public void showAllCitySuccessful2(){
		Collection<TCity> c = as.showAll();
		assertTrue(c.isEmpty());
	}

	//showClientsByCity method
	@Test
	public void showClientsByCitySuccessful(){
        Date initD = new Date(1540373530000L);
        Date endD = new Date(1543051930000L);

        tr.setDateFrom(initD);
        tr.setDateTo(endD);

        //data for city 1
		Integer idC = asC.create(tclient);

        Integer idCity = as.create(tc);

        tv.setCity(idCity);
		Integer idV = asV.create(tv);

		tr.setIdClient(idC);
		tr.setIdVehicle(idV);
        asR.create(tr);

        //data for city 2
        tclient.setIdCardNumber("11111111X");
        Integer idCFail = asC.create(tclient);

        tc.setName("Barcelona");
        Integer idCity2 = as.create(tc);

       tv.setCity(idCity2);
        Integer idVFail = asV.create(tv);

        tr.setIdVehicle(idVFail);
        tr.setIdClient(idCFail);
        asR.create(tr);


        Collection<TClient> out = as.showClientsByCity(idCity);

        for(TClient client : out){
            assertEquals(client.getId(), idC); //city 1
            assertNotEquals(client.getId(),idCFail); //city 2
        }
	}

	//update method tests
	@Test
	public void updateCitySuccessful(){
		as.create(tc);

		tc.setName("Barcelona");
		tc.setActive(true);
		Integer id = as.update(tc);
		assertEquals(id,tc.getId());

		TCity tmp = as.show(id);
		assertEquals(tmp.getName(),tc.getName());
	}

	@Test (expected = IncorrectInputException.class)
	public void updateCityIncorrectInput1(){
		tc.setId(0);//id must be > 0
        tc.setActive(true);
		as.update(tc);
	}

	@Test (expected = IncorrectInputException.class)
	public void updateCityIncorrectInput2(){
		tc.setId(-1); //id must be > 0
        tc.setActive(true);
		as.update(tc);
	}

    @Test (expected = IncorrectInputException.class)
    public void updateCityIncorrectInput3(){
        tc.setActive(false); //active must be true on the update operation, to deactivate the entity,
                            // it's necessary to use the drop operation
        as.update(tc);
    }

	@Test (expected = ASException.class)
	public void updateCityNotExists(){
	    tc.setId(32);
	    tc.setActive(true);
		as.update(tc);
	}

}
