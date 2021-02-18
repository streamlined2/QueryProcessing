package start;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;

import entity.beans.Country;
import entity.beans.Location;
import entity.beans.PurchaseOrder;
import entity.beans.OrderItem;
import entity.beans.Person;
import entity.beans.Product;
import entity.definition.DataSource;
import entity.definition.EntitySource;
import entity.persistence.BasicEntityManager;
import math.Cardinal;
import query.definition.Query;
import query.definition.result.SQLQueryResult;
import query.exceptions.QueryException;
import query.processor.BasicQueryProcessor;
import query.processor.SQLServerProcessor;
import query.definition.AggregationProperty;
import query.definition.Entry;

public class Runner {

	private static final String PASSWORD="pass";
	
	public static void main(final String... args) throws QueryException {
		//testQueries(args);
		
		//Class.forName("com.mysql.cj.jdbc.Driver");//-Djdbc.drivers=com.mysql.cj.jdbc.Driver
	    try {
			  
	    	  final Properties info=new Properties();
			  info.load(new FileReader("local.properties"));
			  
			  //final String uri=System.getProperty("uri");//-DdbURI=jdbc:mysql://localhost:3306/mydb?serverTimezone=UTC 
			  //info.put("user", System.getProperty("user"));
			  //info.put("password", System.getProperty("password"));
			  try (final Connection conn=DriverManager.getConnection(info.getProperty("uri"),info)){
				  
				  try(final BasicEntityManager eM=new BasicEntityManager(conn)){
					  
					  Product productA;
					  OrderItem orderItem;
					  eM.persist(productA=new Product(LocalDateTime.now(),"Apple",BigDecimal.valueOf(5),Product.Status.IN_STOCK));
					  eM.persist(orderItem=new OrderItem(4,productA));
					  eM.persist(orderItem=new OrderItem(1,productA));
					  
					  eM.persist(productA=new Product(LocalDateTime.now(),"Orange",BigDecimal.valueOf(4),Product.Status.RUNNING_LOW));
					  eM.persist(orderItem=new OrderItem(5,productA));
					  eM.persist(orderItem=new OrderItem(3,productA));
					  
					  eM.persist(productA=new Product(LocalDateTime.now(),"Tomato",BigDecimal.valueOf(3),Product.Status.IN_STOCK));
					  eM.persist(orderItem=new OrderItem(2,productA));
					  eM.persist(orderItem=new OrderItem(2,productA));
					  
					  eM.persist(productA=new Product(LocalDateTime.now(),"Pear",BigDecimal.valueOf(7),Product.Status.OUT_OF_STOCK));
					  eM.persist(orderItem=new OrderItem(4,productA));
					  eM.persist(orderItem=new OrderItem(2,productA));
					  
					  eM.persist(productA=new Product(LocalDateTime.now(),"Banana",BigDecimal.valueOf(8),Product.Status.RUNNING_LOW));
					  eM.persist(orderItem=new OrderItem(7,productA));
					  eM.persist(orderItem=new OrderItem(8,productA));
					  
					  eM.persist(productA=new Product(LocalDateTime.now(),"Cucumber",BigDecimal.valueOf(6),Product.Status.IN_STOCK));
					  eM.persist(orderItem=new OrderItem(9,productA));
					  eM.persist(orderItem=new OrderItem(4,productA));
					  
					  eM.persist(productA=new Product(LocalDateTime.now(),"Tangerine",BigDecimal.valueOf(5),Product.Status.IN_STOCK));
					  eM.persist(orderItem=new OrderItem(8,productA));
					  eM.persist(orderItem=new OrderItem(5,productA));
					  
					  eM.persist(productA=new Product(LocalDateTime.now(),"Lemon",BigDecimal.valueOf(4),Product.Status.IN_STOCK));
					  eM.persist(orderItem=new OrderItem(4,productA));
					  eM.persist(orderItem=new OrderItem(3,productA));
					  
					  eM.persist(productA=new Product(LocalDateTime.now(),"Potato",BigDecimal.valueOf(1),Product.Status.RUNNING_LOW));
					  eM.persist(orderItem=new OrderItem(2,productA));
					  eM.persist(orderItem=new OrderItem(4,productA));
					  
					  eM.persist(productA=new Product(LocalDateTime.now(),"Cabbage",BigDecimal.valueOf(2),Product.Status.IN_STOCK));
					  eM.persist(orderItem=new OrderItem(3,productA));
					  eM.persist(orderItem=new OrderItem(3,productA));
					  
					  eM.persist(productA=new Product(LocalDateTime.now(),"Carrot",BigDecimal.valueOf(3),Product.Status.OUT_OF_STOCK));
					  eM.persist(orderItem=new OrderItem(5,productA));
					  eM.persist(orderItem=new OrderItem(7,productA));
					  
					  eM.persist(productA=new Product(LocalDateTime.now(),"Onion",BigDecimal.valueOf(2),Product.Status.RUNNING_LOW));
					  eM.persist(orderItem=new OrderItem(6,productA));
					  eM.persist(orderItem=new OrderItem(6,productA));
					  
					  eM.persist(productA=new Product(LocalDateTime.now(),"Watermelon",BigDecimal.valueOf(4),Product.Status.IN_STOCK));
					  eM.persist(orderItem=new OrderItem(7,productA));
					  eM.persist(orderItem=new OrderItem(9,productA));
					  //System.out.println(productA);
					  
					  productA.setValue("name", "Melon");
					  eM.merge(productA);
					  //System.out.println(productA);
					  
					  orderItem.setValue("quantity", 10);
					  eM.merge(orderItem);
					  //System.out.println(orderItem);
					  
					  Optional<Product> productB=eM.find(Product.class,productA.id());
					  //System.out.println(productB);
					  Optional<OrderItem> orderItemB=eM.find(OrderItem.class,orderItem.id());
					  //System.out.println(orderItemB);
					  
					  final Query query1=new Query();
					  query1.addEntry(Product.class).select("name","price","status");
						
					  final SQLQueryResult query1Result=new SQLServerProcessor(eM,query1).fetch();
					  System.out.printf("\n1'st SQL statement: %s\nProducts: fetched %d record(s)\n%s\n",
							  query1Result.getSQLStatement(),
							  query1Result.getTupleCount(),
							  query1Result);
						  
					  final Query query2=new Query();
					  final Entry<Product> productEntry2=query2.addEntry(Product.class);
					  final Entry<OrderItem> orderItemEntry2=query2.addEntry(OrderItem.class);
					  productEntry2.select("name","price","status","createdAt");
					  final AggregationProperty<OrderItem,Cardinal> quantityTotal=new AggregationProperty<>(orderItemEntry2,"quantity",AggregationProperty.INTEGER_TOTAL);
					  orderItemEntry2.aggregate(quantityTotal);//"quantity",AggregationProperty.INTEGER_TOTAL
					  productEntry2.groupBy("name");
					  orderItemEntry2.sortByDesc(quantityTotal);
						
					  final SQLQueryResult query2Result=new SQLServerProcessor(eM,query2).fetch();
					  System.out.printf("\n2'nd SQL statement: %s\nProducts: fetched %d record(s)\n%s\n",
							  query2Result.getSQLStatement(),
							  query2Result.getTupleCount(),
							  query2Result);
						  
					  //eM.remove(Product.class,productA.id());

					  System.out.println("Please enter password to purge data:");
					  Scanner scan=new Scanner(System.in);
					  String password=scan.next();
					  if(PASSWORD.equals(password)) {
						  System.out.println();
						  System.out.println(eM.removeAll(Product.class));
						  System.out.println(eM.removeAll(OrderItem.class));
						  System.out.println(eM.removeAll(PurchaseOrder.class));
						  System.out.println("Data were purged successfully.");
					  }else {
						  System.out.println("You entered wrong password.");
					  }

				  } catch (Exception e) {
					e.printStackTrace();
				}
			  } catch (SQLException e) {
				  e.printStackTrace(); 
			  }finally {
			  }
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//System.out.println(EntityInspector.analyzeEntity(Person.class));
		//System.out.println(EntityInspector.analyzeEntity(Country.class));
		//System.out.println(EntityInspector.analyzeEntity(Location.class));
		
		  /*
				  final DatabaseMetaData meta=conn.getMetaData();
				  System.out.printf("%s %s %s\n",
						  meta.getDatabaseProductName(),
						  meta.getDatabaseMajorVersion(),
						  meta.getDatabaseMinorVersion());
				  
				  ResultSet schemas=meta.getCatalogs(); 
				  System.out.println("catalogs:");
				  for(;schemas.next();) { System.out.println(schemas.getObject(1)); }
				  System.out.println("catalog separator: "+meta.getCatalogSeparator());
				  System.out.println("catalog term: "+meta.getCatalogTerm());
				  
			  ResultSet columns=meta.getColumns("db",null,"country",null);
			  System.out.println("columns for country:"); 
			  for(;columns.next();) {
				  System.out.println(columns.getObject(4));//1 - db,2 - null,3 - country,4 -name of field 
			  }
			  
			  ResultSet primaryKeys=meta.getPrimaryKeys("db", null, "country");
			  System.out.println("primary keys:"); 
			  for(;primaryKeys.next();) {
				  System.out.println(primaryKeys.getObject(4));//1 - db, 2 - null, 3 - country,  4 - id 
			  }
			  
			  ResultSet tables=meta.getTables("db", null, null, new String[] {"TABLE"});
			  System.out.println("tables: "); for(;tables.next();) {
				  System.out.println(tables.getObject(3));//1 - db, 2 - null, 3 - table name(country,location) 
			  }
			  
			  try(final Statement state=conn.createStatement()){ 
				  try(final ResultSet rs=state.executeQuery("select * from country;")){//db. 
					  for(;rs.next();) {
						  System.out.printf("%d %s\n",rs.getInt(1),rs.getString(2)); 
					  } 
				  } 
			  }
			  
		 */
		
		/*
		 * Set<Class<? extends Entity>> entityClasses=EntityInspector.getEntityBeans();
		 * entityClasses.forEach(System.out::println);
		 */
	}
	
	@SuppressWarnings("unchecked")
	public static void testQueries(final String[] args) throws QueryException {
		
		//define source data
		final EntitySource<Country> countries=new EntitySource<>(
				new Country("USA",9833520D,328239523),
				new Country("UK",242495D,67886004),
				new Country("France",640679D,67153000));
		
		final EntitySource<Location> locations=new EntitySource<>(
				new Location("Washington",countries.one(Country::name,"USA"),705_749,184_661D,Year.of(1791),new Location.Coordinate(38.9101D, -77.0147D)),
				new Location("New York City",countries.one(Country::name,"USA"),8_175_133,1_212.60D,Year.of(1624),new Location.Coordinate(40.71274D, -74.005974D)),
				new Location("Chicago",countries.one(Country::name,"USA"),2_693_976,606.60D,Year.of(1780),new Location.Coordinate(41.881944D, -87.627778D)),
				new Location("Los Angeles",countries.one(Country::name,"USA"),3_792_621,1_302.06D,Year.of(1835),new Location.Coordinate(34.05D, -118.25D)),
				new Location("Atlanta",countries.one(Country::name,"USA"),420_003,354.22D,Year.of(1847),new Location.Coordinate(33.755D, -84.39D)),
				new Location("Houston",countries.one(Country::name,"USA"),2_100_263,1_739.69D,Year.of(1837),new Location.Coordinate(29.762778D, -95.383056D)),

				new Location("London",countries.one(Country::name,"UK"),8_961_989,1_572D,Year.of(47),new Location.Coordinate(51.507222D, -0.1275D)),
				new Location("Glasgow",countries.one(Country::name,"UK"),633_120,175D,Year.of(580),new Location.Coordinate(55.860916D, -4.251433D)),
				new Location("Bristol",countries.one(Country::name,"UK"),463_400,110D,Year.of(1155),new Location.Coordinate(51.45D, -2.583333D)),
				new Location("Gloucester",countries.one(Country::name,"UK"),129_128,40.54D,Year.of(1541),new Location.Coordinate(51.864444D, -2.244444D)),
				new Location("Birmingham",countries.one(Country::name,"UK"),1_141_816,267.8D,Year.of(600),new Location.Coordinate(52.48D, -1.9025D)),

				new Location("Paris",countries.one(Country::name,"France"),2_148_271,105.4D,Year.of(-52),new Location.Coordinate(48.856613D, 2.352222D)),
				new Location("Bordeaux",countries.one(Country::name,"France"),254_436,49.36D,Year.of(-60),new Location.Coordinate(44.84D, -0.58D)),
				new Location("Marseille",countries.one(Country::name,"France"),855_393,240.62D,Year.of(-600),new Location.Coordinate(43.2964D, 5.37D)),
				new Location("Lyon",countries.one(Country::name,"France"),516_092,47.87D,Year.of(-43),new Location.Coordinate(45.76D, 4.84D))
		);
		
		final EntitySource<Person> persons=new EntitySource<Person>(
				new Person("John","Kurtiss",20,Person.Sex.MALE,locations.one(Location::name,"Washington")),
				new Person("Janet","Small",30,Person.Sex.FEMALE,locations.one(Location::name,"New York City")),
				new Person("David","Cole",35,Person.Sex.MALE,locations.one(Location::name,"Glasgow")),
				new Person("Juliet","Freeman",22,Person.Sex.FEMALE,locations.one(Location::name,"London")),
				new Person("Maurice","Lacroix",25,Person.Sex.MALE,locations.one(Location::name,"Paris")),
				new Person("Genevieve","Odila",15,Person.Sex.FEMALE,locations.one(Location::name,"Bordeaux")));
		
		final DataSource data=new DataSource(
				countries,locations,persons
		);
		
		//define query and its components
		final Query q1=new Query();
		//define data entries for each entity bean
		final Entry<Person> personEntry=q1.addEntry(Person.class);
		final Entry<Location> locationEntry=q1.addEntry(Location.class);
		final Entry<Country> countryEntry=q1.addEntry(Country.class);
		//define select properties
		personEntry.select(Person::lastname,Person::firstname,Person::age,Person::sex);
		countryEntry.select(Country::name,Country::population);
		locationEntry.select(Location::name,Location::founded);
		//define sort by properties
		countryEntry.sortBy(Country::name);
		personEntry.sortBy(Person::lastname,Person::firstname);
		locationEntry.sortBy(Location::name);
		//define filter conditions
		//countryEntry.where((Country c)->c.name().equals("USA"));
		//locationEntry.where((Location loc)->loc.name().startsWith("W"));
		personEntry.where((Person p)->p.firstname().startsWith("J"));

		//System.out.println(q1);
		
		System.out.printf("Query #1> join person/location/country entities, select fields, filter tuples and order result: \n%s\n\n",new BasicQueryProcessor(q1,data).fetch());	

		final Query q2=new Query();
		final Entry<Country> c=q2.addEntry(Country.class);
		final Entry<Location> l=q2.addEntry(Location.class);
		c.select(Country::name);
		l.aggregate(Location::population,AggregationProperty.INTEGER_COUNT);
		l.aggregate(Location::population,AggregationProperty.INTEGER_TOTAL);
		l.aggregate(Location::area,AggregationProperty.DOUBLE_TOTAL);
		l.aggregate(Location::area,AggregationProperty.DOUBLE_AVERAGE);
		//c.sortBy(Country::name);
		c.groupBy(Country::name);
		System.out.printf("Query #2> fetch count and population total, area total and average for each country:\n%s\n\n",new BasicQueryProcessor(q2,data).fetch());
		
	}

}
