package br.edu.ifsp.neo4j.dao;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import br.edu.ifsp.dao.DAOManager;
import br.edu.ifsp.model.MyImage;
import br.edu.ifsp.neo4j.connection.Neo4jJDBCConnection;

public class ReadFromDataBaseToFileTest {

	@Test
	public void FromDataBaseToFileTest() throws SQLException {

		MyImage myImage = null;

		int imageId = 1;

		String query = "MATCH (n:MyImage { imageId : ?}) RETURN n.imageId, n.imageName, n.imageBytes";

		Neo4jJDBCConnection neo4jConnection = new Neo4jJDBCConnection();

		PreparedStatement preparedStatement = neo4jConnection.connect().prepareStatement(query);

		preparedStatement.setInt(1, imageId);

		ResultSet resultSet = neo4jConnection.executeQuery(preparedStatement);

		if (resultSet.next()) {

			myImage = new MyImage();

			System.out.println(resultSet.getInt("n.imageId"));

			System.out.println(resultSet.getString("n.imageName"));

			String imageBytes = resultSet.getString("n.imageBytes");

			System.out.println(imageBytes.length());

			myImage.setImageId(resultSet.getInt("n.imageId"));

			myImage.setImageName(resultSet.getString("n.imageName"));
			
			myImage.setImageBytes(Base64.decodeBase64(imageBytes));
			
			try {
				assertTrue(DAOManager.myImageDAONeo4J().byteArrayToTiffFile(myImage));
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}

			System.out.println(myImage.getImageBytes().length);

		}

		assertTrue(myImage.getImageId() == 1);

		assertTrue(myImage.getImageName().equals("DCC.TIFF"));

		assertTrue(myImage.getImageBytes().length == 11487232);

		neo4jConnection.disconnect();
	}

}
