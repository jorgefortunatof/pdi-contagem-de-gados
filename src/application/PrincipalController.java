package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import util.*;


public class PrincipalController {
	
	@FXML private Pane color;
	
	@FXML private Label lblR;
	@FXML private Label lblG;
	@FXML private Label lblB;
	
	@FXML private ImageView imageView1;
	@FXML private ImageView imageView3;
	
	@FXML private TextField gaussiano;
	@FXML private TextField erozao;
	@FXML private TextField limiarizacao;
	
	@FXML private Label alert;
	@FXML private Label contagem;

	
	private Image image1;
	private Image image3;
	
	private File f;
	
	
	private File selecionarImagem() {
		FileChooser fileChooser = new FileChooser();
		
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
				"Imagens", "*.jpg", "*.JPG", "*.png", ".PNG", "*.gif", ".GIF", "*.bmp", "*.BMP", "*.jpeg", "*.JPEG"));
		
		File imgSelec = fileChooser.showOpenDialog(null);
		
		try {
			if(imgSelec != null) {
				return imgSelec;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private void verificaCor(Image img, int x, int y) {
		
		try {
		 Color cor = img.getPixelReader().getColor(x-1, y-1);
		 
		 int red = (int) (cor.getRed()*255);
		 int blue = (int) (cor.getBlue()*255);
		 int green = (int) (cor.getGreen()*255);
 
		 lblR.setText("R: " + red);
		 lblB.setText("B: " + blue);
		 lblG.setText("G: " + green);
		 
		 color.setStyle("-fx-background-color: rgb("+red+","+green+","+blue+");");
		}catch(Exception e) {
//			e.printStackTrace();
		}
	}
	
	private void atualizaImagem3() {
		imageView3.setImage(image3);
		imageView3.setFitHeight(image3.getHeight());
		imageView3.setFitWidth(image3.getWidth());
	}
	
		
	@FXML public void abreImagem1() {
		f = selecionarImagem();
		if(f != null) {
			image1 = new Image(f.toURI().toString());
			imageView1.setImage(image1);
			imageView1.setFitHeight(image1.getHeight());
			imageView1.setFitWidth(image1.getWidth());	
		}
	}
		

	@FXML public void rasterImg(MouseEvent evt) {
		ImageView iv = (ImageView)evt.getTarget();
		
		if(iv.getImage() != null) {
			verificaCor(iv.getImage(), (int)evt.getX(), (int)evt.getY());
		}
	}
	
	@FXML public void salvar(){
		if (image3 != null){
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(
					new FileChooser.ExtensionFilter("Imagem", "*.png")); 	

			fileChooser.setInitialDirectory(new File("/Users/jorge/Desktop/"));
			
			File file = fileChooser.showSaveDialog(null);
			if (file !=null){
				BufferedImage bImg = SwingFXUtils.fromFXImage(image3, null);
				try {
					ImageIO.write(bImg, "PNG", file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{
			System.out.println("Não há nenhuma imagem modificada");
		}
	}
	
	
	@FXML public void erozao() {
		if(image1 != null) {
			image3 = PDIOpenCv.erozao(image1);
			atualizaImagem3();	
		}
	}
	
	@FXML public void dilatacao() {
		if(image1 != null) {
			image3 = PDIOpenCv.dilatacao(image1);
			atualizaImagem3();	
		}
	}
	
	@FXML public void ecalaDeCinza() {
		if(image1 != null) {
			image3 = PDIOpenCv.escalaDeCinza(image1);
			atualizaImagem3();	
		}
	}
	
	@FXML public void filtroGaussiano() {
		if(image1 != null) {
			image3 = PDIOpenCv.filtroGaussiano(image1);
			atualizaImagem3();	
		}
	}
	
	@FXML public void limiarizacao() {
		if(image1 != null) {
			image3 = PDIOpenCv.limiarizacao(image1);
			atualizaImagem3();	
		}
	}
	
	
	@FXML public void filtrosContagem() {
		alert.setText("");
		
		if(image1 != null) {
			int erozaoVal = Integer.parseInt(erozao.getText()); 
			int gaussianoVal = Integer.parseInt(gaussiano.getText()); 
			int limiarizacaoVal = Integer.parseInt(limiarizacao.getText()); 
			
			if(erozaoVal > -1 && gaussianoVal > 0 && limiarizacaoVal > -1) {
				if(gaussianoVal % 2 == 1) {
					image3 = PDIOpenCv.filtrosContagem(image1, erozaoVal, gaussianoVal, limiarizacaoVal, contagem);
					atualizaImagem3();	
				}else {
					alert.setText("O valor do filtro gaussiano deve ser impar!");
				}
			}else {
				alert.setText("Insira numeros válidos maiores que zero!");
			}
		}else {
			alert.setText("Selecione uma imagem!");	
		}
	}

}
