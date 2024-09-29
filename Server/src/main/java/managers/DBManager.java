package managers;

import models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Scanner;
import java.util.TreeSet;


public class DBManager {
    public static final Logger logger = LoggerFactory.getLogger(DBManager.class);
    private final String url = "jdbc:postgresql://localhost:5432/studs";
    private String username;
    private String password;
    private Connection conn;

    public DBManager(String[] args) {
        try {
            var filename = args[0];
            assert filename != null;
            if (filename != null | !filename.isEmpty()) {
                var fileReader = new Scanner(new File(filename));
                File file = new File(args[0]);
                var s = new StringBuilder("");
                String[] db = new String[2];
                byte counter = 0;
                for (int i = 0; i < db.length; i++) {
                    counter += 1;
                    db[i] = fileReader.nextLine();
                }
                if (counter != 2) {
                    logger.error("Неверное количество параметров. Сервер не запущен!");
                    System.exit(1);
                } else {
                    this.username = db[0];
                    this.password = db[1];
                }
            } else {
                logger.error("Файл не найден. Сервер не запущен!");
                System.exit(1);
            }
        } catch (FileNotFoundException e) {
            logger.error("Файл не найден. Сервер не запущен!");
            System.exit(1);
        }
    }

    public void connect() {
        try {
            conn = DriverManager.getConnection(url, username, password);
            logger.info("Подключение к базе данных прошло успешно!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            logger.error("При подключении к базе данных произошла ошибка. Сервер не запущен!");
            System.exit(1);
        }
    }

    public boolean insertProduct(Product product) {
        try {
            Person owner = product.getOwner();
            if (owner != null) {
                PreparedStatement stmt = conn.prepareStatement("insert into locations(x,y,name) values(?,?,?) returning id");
                Location location = product.getOwner().getLocation();
                stmt.setLong(1, location.getX());
                stmt.setLong(2, location.getY());
                stmt.setString(3, location.getName());

                ResultSet rs = stmt.executeQuery();
                rs.next();

                stmt = conn.prepareStatement("insert into owners(name,height,eyecolor,haircolor,nationality,location) values(?,?,?,?,?,?) returning id");
                stmt.setString(1, owner.getName());
                stmt.setFloat(2, owner.getHeight());
                stmt.setString(3, owner.getEyeColor().toString());
                stmt.setString(4, owner.getHairColor().toString());
                stmt.setString(5, owner.getNationality().toString());
                stmt.setInt(6, rs.getInt("id"));
                rs = stmt.executeQuery();
                rs.next();

                stmt = conn.prepareStatement("insert into products(name,x,y,creation,price,partnumber,cost, unitofmeasure,creator,owner) values(?,?,?,?,?,?,?,?,?,?)  ");
                stmt.setString(1, product.getName());
                stmt.setInt(2, product.getCoordinates().getX());
                stmt.setDouble(3, product.getCoordinates().getY());
                stmt.setDate(4, java.sql.Date.valueOf(product.getCreationDate()));
                stmt.setFloat(5, product.getPrice());
                stmt.setString(6, product.getPartNumber());
                stmt.setFloat(7, product.getManufactureCost());
                stmt.setString(8, product.getUnitOfMeasure().toString());
                stmt.setString(9, product.getCreator());
                stmt.setInt(10, rs.getInt("id"));
                stmt.executeUpdate();
                stmt.close();

            } else {
                PreparedStatement stmt = conn.prepareStatement("insert into products(name,x,y,creation,price,partnumber,cost, unitofmeasure,creator,owner) values(?,?,?,?,?,?,?,?,?,?)  ");
                stmt.setString(1, product.getName());
                stmt.setInt(2, product.getCoordinates().getX());
                stmt.setDouble(3, product.getCoordinates().getY());
                stmt.setDate(4, java.sql.Date.valueOf(product.getCreationDate()));
                stmt.setFloat(5, product.getPrice());
                stmt.setString(6, product.getPartNumber());
                stmt.setFloat(7, product.getManufactureCost());
                stmt.setString(8, product.getUnitOfMeasure().toString());
                stmt.setString(9, product.getCreator());
                stmt.setNull(10, Types.NULL);
                stmt.close();
            }
            return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage() + e.getErrorCode());
            return false;
        }

    }

    public int getFreeId() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT last_value FROM products_id_seq;");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else return -1;
        } catch (SQLException e) {
            return -1;
        }
    }

    synchronized public void loadCollection(TreeSet<Product> collection) {
        try {
            collection.clear();
            PreparedStatement stmt = conn.prepareStatement("select * from products join owners on products.owner= owners.id join locations on owners.location = locations.id");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Person owner = null;
                int productId = rs.getInt(1);
                String productName = rs.getString(2);
                int x = rs.getInt(3);
                double y = rs.getDouble(4);
                OffsetDateTime offsetDateTime = rs.getObject(5,OffsetDateTime.class);
                LocalDateTime creationDate = offsetDateTime.toLocalDateTime();
                float price = rs.getFloat(6);
                String partNumber = rs.getString(7);
                int cost = rs.getInt(8);
                UnitOfMeasure unitOfMeasure = UnitOfMeasure.valueOf(rs.getString(9));
                String creator = rs.getString(10);
                Integer ownerId = rs.getInt(11);
                if (ownerId != null) {
                    String ownerName = rs.getString(13);
                    float ownerHeight = rs.getFloat(14);
                    EyeColor eyeColor = EyeColor.valueOf(rs.getString(15));
                    HairColor hairColor = HairColor.valueOf(rs.getString(16));
                    Country nationality = Country.valueOf(rs.getString(17));
                    long locationX = rs.getLong(18);
                    long locationY = rs.getLong(19);
                    String locationName = rs.getString(20);
                    owner = new Person(ownerName, ownerHeight, eyeColor, hairColor, nationality, new Location(locationX, locationY, locationName));
                }
                Product product = new Product(productId, productName, new Coordinates(x, y), creationDate, price, partNumber, cost, unitOfMeasure, owner, creator);
                collection.add(product);
            }

        } catch (SQLException e) {
            collection = new TreeSet<>();
        }
    }

    public Product getById(int id) {
        try {
            PreparedStatement stmt = conn.prepareStatement("select * from products join owners on products.owner= owners.id join locations on owners.location = locations.id where products.id=?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Person owner = null;
                int productId = rs.getInt(1);
                String productName = rs.getString(2);
                int x = rs.getInt(3);
                double y = rs.getDouble(4);
                OffsetDateTime offsetDateTime = rs.getObject(5,OffsetDateTime.class);
                LocalDateTime creationDate = offsetDateTime.toLocalDateTime();
                float price = rs.getFloat(6);
                String partNumber = rs.getString(7);
                int cost = rs.getInt(8);
                UnitOfMeasure unitOfMeasure = UnitOfMeasure.valueOf(rs.getString(9));
                String creator = rs.getString(10);
                Integer ownerId = rs.getInt(11);
                if (ownerId != null) {
                    String ownerName = rs.getString(13);
                    float ownerHeight = rs.getFloat(14);
                    EyeColor eyeColor = EyeColor.valueOf(rs.getString(15));
                    HairColor hairColor = HairColor.valueOf(rs.getString(16));
                    Country nationality = Country.valueOf(rs.getString(17));
                    long locationX = rs.getLong(18);
                    long locationY = rs.getLong(19);
                    String locationName = rs.getString(20);
                    owner = new Person(ownerName, ownerHeight, eyeColor, hairColor, nationality, new Location(locationX, locationY, locationName));
                }
                return new Product(productId, productName, new Coordinates(x, y), creationDate, price, partNumber, cost, unitOfMeasure, owner, creator);
            } else return null;


        } catch (SQLException e) {
            return null;
        }
    }

    public boolean deleteById(int id) {
        try {
            PreparedStatement stmt = conn.prepareStatement("delete from products where id = ? returning owner");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stmt = conn.prepareStatement("delete from owners where id = ? returning location");
                stmt.setLong(1, rs.getLong(1));
                ResultSet rs2 = stmt.executeQuery();
                if (rs2.next()) {
                    stmt = conn.prepareStatement("delete from locations where id = ?");
                    stmt.setLong(1, rs.getLong(1));
                    stmt.executeUpdate();
                    return true;
                } else return false;

            } else return false;

        } catch (SQLException e) {
            return false;
        }

    }

    public ResultSet executeQuery(String sql) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            return null;
        }
    }


    public boolean registerUser(String username, String password) {
        try {
            PreparedStatement st = conn.prepareStatement("insert into users(login,password) values(?,?)");
            st.setString(1, username);
            st.setString(2, password);
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean exists(String login, String password) {
        try {
            PreparedStatement st = conn.prepareStatement("select password from users WHERE login=?");
            st.setString(1, login);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                if (password.equals(rs.getString(1))) return true;
                else return false;
            } else return false;
        } catch (SQLException e) {
            return false;
        }

    }

    public boolean backupDatabase(String backupFilePath) {
        try {
            String command = String.format("pg_dump -U %s -F c -b -v -f %s %s", username, backupFilePath, url);
            Process p = Runtime.getRuntime().exec(command);
            int processComplete = p.waitFor();
            return processComplete == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean update(int id, Product newProduct) {
        try {
            if (newProduct.getOwner() != null) {
                String stmt = "update products set name=?,x=?,y=?,creation=?,price=?,partnumber=?,cost=?,unitofmeasure=?,creator=? where id=? returning owner";
                PreparedStatement st = conn.prepareStatement(stmt);
                st.setString(1, newProduct.getName());
                st.setInt(2, newProduct.getCoordinates().getX());
                st.setDouble(3, newProduct.getCoordinates().getY());
                st.setDate(4, java.sql.Date.valueOf(newProduct.getCreationDate()));
                st.setFloat(5, newProduct.getPrice());
                st.setString(6, newProduct.getPartNumber());
                st.setFloat(7, newProduct.getManufactureCost());
                st.setString(8, newProduct.getUnitOfMeasure().toString());
                st.setString(9, newProduct.getCreator());
                st.setInt(10, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    stmt = "update owners set name=?, height=?, eyecolor=?, haircolor=?, nationality=? where id=? returning location";
                    st = conn.prepareStatement(stmt);
                    st.setString(1, newProduct.getOwner().getName());
                    st.setFloat(2, newProduct.getOwner().getHeight());
                    st.setString(3, newProduct.getOwner().getEyeColor().toString());
                    st.setString(4, newProduct.getOwner().getHairColor().toString());
                    st.setString(5, newProduct.getOwner().getNationality().toString());
                    st.setLong(6, rs.getInt("owner"));
                    rs = st.executeQuery();
                    if (rs.next()) {
                        stmt = "update locations set x=?, y=?, name=? where id=?";
                        st = conn.prepareStatement(stmt);
                        st.setLong(1, newProduct.getOwner().getLocation().getX());
                        st.setLong(2, newProduct.getOwner().getLocation().getY());
                        st.setString(3, newProduct.getOwner().getLocation().getName());
                        st.setInt(4, rs.getInt("location"));
                        return true;

                    } else return false;

                } else return false;
            } else {
                String stmt = "update products set name=?,x=?,y=?,creation=?,price=?,partnumber=?,cost=?,unitofmeasure=?,creator=? where id=? returning owner";
                PreparedStatement st = conn.prepareStatement(stmt);
                st.setString(1, newProduct.getName());
                st.setInt(2, newProduct.getCoordinates().getX());
                st.setDouble(3, newProduct.getCoordinates().getY());
                st.setDate(4, java.sql.Date.valueOf(newProduct.getCreationDate()));
                st.setFloat(5, newProduct.getPrice());
                st.setString(6, newProduct.getPartNumber());
                st.setFloat(7, newProduct.getManufactureCost());
                st.setString(8, newProduct.getUnitOfMeasure().toString());
                st.setString(9, newProduct.getCreator());
                st.setInt(10, id);
                st.executeUpdate();
                return true;
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    public boolean clear(String login) {
        try {
            PreparedStatement stmt = conn.prepareStatement("delete from products where owner = ? ");
            stmt.setString(1, login);
            stmt.executeQuery();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
