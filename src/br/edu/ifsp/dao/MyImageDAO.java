package br.edu.ifsp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.bson.Document;
import org.bson.types.Binary;

import com.mongodb.client.FindIterable;

import br.edu.ifsp.connection.AConnection;
import br.edu.ifsp.model.MyImage;

public class MyImageDAO extends ImageFileDAO implements IDAO<MyImage> {

	private AConnection connection;

	public MyImageDAO(AConnection connection) {

		this.connection = connection;

	}

	@Override
	public boolean insert(MyImage myImage) {

		boolean insert = false;

		this.connection.connect();

		Map<String, Object> values = new HashMap<String, Object>();

		values.put("imageId", myImage.getImageId());
		values.put("imageName", myImage.getImageName());
		values.put("imageBytes", myImage.getImageBytes());

		insert = connection.executeInsert("MyImage", values);

		this.connection.disconnect();

		return insert;

	}

	@Override
	public boolean update(MyImage myImage) {

		boolean update = false;

		this.connection.connect();

		Map<String, Object> values = new HashMap<String, Object>();

		values.put("imageId", myImage.getImageId());
		values.put("imageName", myImage.getImageName());
		values.put("imageBytes", myImage.getImageBytes());

		update = connection.executeUpdate("MyImage", values);

		this.connection.disconnect();

		return update;

	}

	@Override
	public boolean delete(int imageId) {

		boolean delete = false;

		this.connection.connect();

		delete = connection.executeDelete("MyImage", "imageId", imageId);

		this.connection.disconnect();

		return delete;

	}

	@Override
	public MyImage search(int imageId) throws SQLException {

		this.connection.connect();

		MyImage myImage = null;

		Object queryResult = connection.executeSearch("MyImage", "imageId", imageId);

		if (queryResult != null) {

			if (queryResult instanceof ResultSet) {

				ResultSet resultSet = (ResultSet) queryResult;

				if (resultSet.next()) {

					myImage = new MyImage();

					myImage.setImageId(resultSet.getInt("imageId"));

					myImage.setImageName(resultSet.getString("imageName"));
					
					Object object = resultSet.getObject("imageBytes");
					
					if (object instanceof String) {
						
						myImage.setImageBytes(Base64.decodeBase64((String) object));
						
					} else {
						
						myImage.setImageBytes((byte[]) object);
						
					}

				}

				((ResultSet) queryResult).close();

				resultSet.close();

				this.connection.disconnect();

			} else {

				Document document = (Document) queryResult;

				myImage = new MyImage();

				myImage.setImageId((int) document.get("imageId"));

				myImage.setImageName((String) document.get("imageName"));
				
				Binary binary = (Binary) document.get("imageBytes");

				byte[] imageBytes = binary.getData();

				myImage.setImageBytes((imageBytes));

				this.connection.disconnect();

			}

		}

		return myImage;
	}

	@Override
	public List<MyImage> list() throws SQLException {

		this.connection.connect();

		List<MyImage> myImageList = new ArrayList<>();

		Object queryResult = connection.executeListData("MyImage");

		MyImage myImage = null;

		if (queryResult != null) {

			if (queryResult instanceof ResultSet) {

				ResultSet resultSet = (ResultSet) queryResult;

				while (resultSet.next()) {

					myImage = new MyImage();

					myImage.setImageId(resultSet.getInt("imageId"));

					myImage.setImageName(resultSet.getString("imageName"));
					
					Object object = resultSet.getObject("imageBytes");
					
					if (object instanceof String) {
						
						myImage.setImageBytes(Base64.decodeBase64((String) object));
						
					} else {
						
						myImage.setImageBytes((byte[]) object);
						
					}

					myImageList.add(myImage);

				}

				((ResultSet) queryResult).close();

				resultSet.close();

				this.connection.disconnect();

			} else {

				@SuppressWarnings("unchecked")
				FindIterable<Document> findIterable = (FindIterable<Document>) queryResult;
				
				Document document;

				while (findIterable.iterator().hasNext()) {

					document = findIterable.iterator().next();

					myImage = new MyImage();

					myImage.setImageId((int) document.get("imageId"));

					myImage.setImageName((String) document.get("imageName"));

					byte[] imageBytes = (byte[]) document.get("imageBytes");

					myImage.setImageBytes((imageBytes));

					myImageList.add(myImage);

				}

				this.connection.disconnect();

			}

		}

		return myImageList;

	}

}
