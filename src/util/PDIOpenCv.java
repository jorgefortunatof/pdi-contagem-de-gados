package util;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;

import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;


public class PDIOpenCv {
	
	public static Image escalaDeCinza(Image imagem) {
		Mat mat = imageToMat(imagem);
		Mat outImage = new Mat();
		
		Imgproc.cvtColor(mat, outImage, Imgproc.COLOR_BGR2GRAY);
		
		return matToImage(outImage);
	}

	public static Image erozao(Image imagem) {
        Mat mat = imageToMat(imagem);
        
        Mat structImage = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(15,15));
        Mat outImage = new Mat();
        
        Imgproc.erode(mat,outImage,structImage);
        
        return matToImage(outImage);
	}
	
	public static Image dilatacao(Image imagem) {
		Mat mat = imageToMat(imagem);
		Mat structImage = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(15,15));
		
		Mat outImage = new Mat();
		Imgproc.dilate(mat,outImage,structImage);
		
		return matToImage(outImage);
	}

	public static Image filtroGaussiano(Image imagem) {
		Mat mat = imageToMat(imagem);
		Mat outImage = new Mat();


		Imgproc.GaussianBlur(mat, outImage, new Size(5, 5), 0);
		return matToImage(outImage);
	}
    
	public static Image limiarizacao(Image imagem) {
		Mat mat = imageToMat(imagem);
		Mat outImage = new Mat();
		
		Imgproc.threshold(mat, outImage, 130, 255, Imgproc.THRESH_BINARY);

		return matToImage(outImage);
	}
	
	public static Image filtrosContagem(Image imagem, int erozao, int gaussiano, int limiarizacao, Label contagemLabel) {
        
        Mat mat = imageToMat(imagem);
		Mat outImage = new Mat();
		
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(mat, mat, new Size(gaussiano, gaussiano), 0);
        Mat structImage = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(erozao, erozao));
        Imgproc.erode(mat,mat,structImage);
		Imgproc.threshold(mat, outImage, limiarizacao, 255, Imgproc.THRESH_BINARY);

		int contagem = contagemDeObjetos(outImage);
		contagemLabel.setText("Contagem: " + contagem);
		
		return matToImage(outImage);
	}
	
	
    //DETECÇÃO DE BORDAS
    public static Image canny(Image imagem) {
		try {
			Mat matImgDst = new Mat();
			Mat matImgSrc = imageToMat(imagem);

			Imgproc.Canny(matImgSrc, matImgDst, 10, 100);
			
			return matToImage(matImgDst);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Image sobel(Image imagem) {
		// matriz do arquivo de entrada
		Mat matImgSrc = imageToMat(imagem);

		Mat grayMat = new Mat(); // guardar resultado de preto e branco
		Mat sobel = new Mat(); // guardar resultado

		Mat grad_x = new Mat();
		Mat abs_grad_x = new Mat();

		Mat grad_y = new Mat();
		Mat abs_grad_y = new Mat();

		// converte para preto e branco
		Imgproc.cvtColor(matImgSrc, grayMat, Imgproc.COLOR_BGR2GRAY);

		// calcula gradiente na dire��o horizontal
		Imgproc.Sobel(grayMat, grad_x, CvType.CV_16S, 1, 0, 3, 1, 0);

		// calcula gradiente na dire��o vertical
		Imgproc.Sobel(grayMat, grad_y, CvType.CV_16S, 0, 1, 3, 1, 0);

		// calcula valores absolutos de gradiente nas duas dire��es
		Core.convertScaleAbs(grad_x, abs_grad_x);
		Core.convertScaleAbs(grad_y, abs_grad_y);

		// calculando o gradiente resultante
		Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 1, sobel);

		
		return matToImage(sobel);
	}

	public static Image laplace(Image imagem) {
		Mat src, src_gray = new Mat(), dst = new Mat();
		int kernel_size = 3;
		int scale = 1;
		int delta = 0;
		int ddepth = CvType.CV_16S;

		src = imageToMat(imagem);

		Imgproc.GaussianBlur(src, src, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);

		Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_RGB2GRAY);

		Mat abs_dst = new Mat();
		Imgproc.Laplacian(src_gray, dst, ddepth, kernel_size, scale, delta, Core.BORDER_DEFAULT);

		Core.convertScaleAbs(dst, abs_dst);

		return matToImage(abs_dst);
	}

	
    //UTILS
	public static Image matToImage(Mat mat) {
		MatOfByte byteMat = new MatOfByte();
		Imgcodecs.imencode(".bmp", mat, byteMat);
		return new Image(new ByteArrayInputStream(byteMat.toArray()));
	}
	
	public static Mat imageToMat(Image image) {
		int width = (int) image.getWidth();
	    int height = (int) image.getHeight();
	    byte[] buffer = new byte[width * height * 4];

	    PixelReader reader = image.getPixelReader();
	    WritablePixelFormat<ByteBuffer> format = WritablePixelFormat.getByteBgraInstance();
	    reader.getPixels(0, 0, width, height, format, buffer, 0, width * 4);

	    Mat mat = new Mat(height, width, CvType.CV_8UC4);
	    mat.put(0, 0, buffer);
	    return mat;
	}
    
	public static int contagemDeObjetos(Mat mat) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();

		Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		return contours.size();
	}

}
