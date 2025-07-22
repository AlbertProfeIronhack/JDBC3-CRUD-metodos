package com.ironhack.videojuegos;

import java.sql.*;
import java.util.Scanner;

public class GestorVj {

    public void gestorJuegos() {

        // aquí irán todas las consultas mySQL
        // try intentará hacer la conexión y las consultas
        try {
            // 1. Cargar el driver:
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Crear objeto de conexión que conecte con esa BD específica:
            String URL = "jdbc:mysql://localhost:3306/videojuegos?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF8&useSSL=false";
            String user = "root";
            String password = "";
            Connection con1 = DriverManager.getConnection(URL, user, password);

            System.out.println("*** conexión realizada! ***");

            boolean salir = false;
            int opcion;
            Scanner sc = new Scanner(System.in);

            while (!salir) {
                System.out.println(
                        "\nElige opción:\n1-Ver todo\n2-Insertar Juego\n3- Modificar Juego\n4- Borrar Juego\n5- Salir");
                opcion = sc.nextInt();
                sc.nextLine();

                switch (opcion) {
                    case 1:
                        mostrarListado(con1);
                        break;
                    case 2:
                        insertarJuego(con1, sc);
                        break;
                    case 3:
                        modificarJuego(con1, sc);
                        break;
                    case 4:
                        borrarJuego(con1, sc);
                        break;
                    case 5:
                        System.out.println("hasta la vista, baby!");
                        salir = true;
                        break;
                    default:
                        System.out.println("debes escribir un número entre 1 y 5");
                }

            }

            // sc.close();

        } catch (Exception e) {
            System.out.println(" *** algo no funciona :-( ***");
            e.printStackTrace();
        }

    }

    // Metodo Mostrar Listado:
    public static void mostrarListado(Connection con1) {
        // try-with-resources cierra todas las conexiones al salir
        // se ponen los recursos dentro del paréntesis del try()
        try (Statement stat1 = con1.createStatement();
                ResultSet resultado1 = stat1.executeQuery("SELECT * FROM juegos")) {
            System.out.println("*** LISTADO DE VIDEOJUEGOS ***");

            while (resultado1.next()) {
                System.out.println("id: " + resultado1.getInt("id") + ": " + resultado1.getString("juegoname")
                        + ", Lanzamiento: " + resultado1.getInt("anio"));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar los juegos " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(" *** algo no funciona :-( ***" + e.getMessage());
            e.printStackTrace();
        }
    }

    // método añadir juego (incluye verificar si el nombre ya existe)
    public static void insertarJuego(Connection con1, Scanner sc) {
        String insert3 = "INSERT INTO juegos(juegoname, anio) VALUES(?, ?)";

        // para ver si existe un juego, hacer una query con count en base al nombre y
        // entonces solo añadir el juego si NO existe

        String verificarNombre = "SELECT COUNT(*) FROM juegos WHERE juegoname = ?"; // cuenta cuantos hay

        try (PreparedStatement ps = con1.prepareStatement(insert3);
                PreparedStatement ps2 = con1.prepareStatement(verificarNombre)) {
            String tituloNuevo = "";
            int anioNuevo = 0;

            while (!tituloNuevo.equalsIgnoreCase("salir")) {
                System.out.println("Escribe el título del juego:");
                tituloNuevo = sc.nextLine();

                if (!tituloNuevo.equalsIgnoreCase("salir")) {
                    // comprobar si existe:
                    ps2.setString(1, tituloNuevo);
                    ResultSet resultadoVerificar = ps2.executeQuery();
                    resultadoVerificar.next(); // saltaria a la primera fila

                    int cuentaCount = resultadoVerificar.getInt(1); // indice columna

                    // si el count es cero, entonces no existe el juego todavia
                    if (cuentaCount == 0) {
                        System.out.println("Escribe el año de lanzamiento");
                        anioNuevo = sc.nextInt();
                        sc.nextLine(); // limpiar buffer

                        ps.setString(1, tituloNuevo); // qué hay en el interrogante 1
                        ps.setInt(2, anioNuevo); // qué hay en el interrogante 2

                        ps.executeUpdate(); // acordaos de esto! aquí es donde se efectúa!

                        System.out.println("*** juego añadido! ***");
                    } else{
                        System.out.println("ese juego ya existe");
                    }

                }
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar el juego " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(" *** algo no funciona :-( ***" + e.getMessage());
            e.printStackTrace();
        }
    }

    // modificar juego:
    public static void modificarJuego(Connection con1, Scanner sc) {
        String modificar = "UPDATE juegos SET juegoname = ? WHERE id= ?";
        String modificar2 = "UPDATE juegos SET anio = ? WHERE id= ?";

        try (
                PreparedStatement ps = con1.prepareStatement(modificar);
                PreparedStatement ps2 = con1.prepareStatement(modificar2);) {
            System.out.println("qué id quieres modificar?");
            int idCliente = sc.nextInt();
            sc.nextLine();
            System.out.println("\nQué deseas modificar?\n1- Titulo\n2- Año Lanzamiento");
            int opcionCambiar = sc.nextInt();
            sc.nextLine();

            switch (opcionCambiar) {
                case 1:
                    System.out.println("introduce el nuevo título:");
                    String nuevoTitulo = sc.nextLine();
                    ps.setString(1, nuevoTitulo);
                    ps.setInt(2, idCliente);
                    ps.executeUpdate();
                    System.out.println("*** titulo cambiado! ***");
                    break;

                case 2:
                    System.out.println("introduce el nuevo año:");
                    int nuevoAnio = sc.nextInt();
                    sc.nextLine();
                    ps2.setInt(1, nuevoAnio);
                    ps2.setInt(2, idCliente);
                    ps2.executeUpdate();
                    System.out.println("*** año cambiado! ***");
                    break;

                default:
                    System.out.println("debes escoger 1 o 2");
            }

        } catch (SQLException e) {
            System.out.println("Error al intentar modificar el juego " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(" *** algo no funciona :-( ***" + e.getMessage());
            e.printStackTrace();
        }
    }

    // borrar juego:
    public static void borrarJuego(Connection con1, Scanner sc) {
        String borrar = "DELETE from juegos WHERE id = ?";

        try (PreparedStatement ps = con1.prepareStatement(borrar);) {
            System.out.println("Qué id tiene el juego que deseas borrar?");
            int idBorrar = sc.nextInt();
            sc.nextLine();
            ps.setInt(1, idBorrar);
            ps.executeUpdate();
            System.out.println("*** juego borrado! ***");

        } catch (SQLException e) {
            System.out.println("Error al intentar borrar el juego " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(" *** algo no funciona :-( ***" + e.getMessage());
            e.printStackTrace();
        }
    }

}
