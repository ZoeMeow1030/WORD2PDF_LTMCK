package controller;

import java.io.IOException;
import java.nio.file.Paths;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import model.bean.PDF2XLS;

// https://openplanning.net/11069/upload-va-download-file-luu-tru-tren-o-cung-voi-java-servlet
@WebServlet(urlPatterns = { "/upload" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
       maxFileSize = 1024 * 1024 * 10, // 10MB
       maxRequestSize = 1024 * 1024 * 50) // 50MB
public class Upload extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String HOME_DIRECTORY = "D:\\Documents\\ltmck";

    @Override
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException
    {
        String urlTarget = "/upload.jsp";
        RequestDispatcher rd = getServletContext().getRequestDispatcher(urlTarget);
		rd.forward(request, response);
    }

    @Override
    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException
    {
        try {
            Part part = request.getPart("file");
            if (part == null)
                throw new Exception("Something went wrong.");

            // If file is invaild name, return.
            if (part.getName() == null || part.getName().isEmpty())
                throw new Exception("File upload is invaild name.");

            // Create new item.
            PDF2XLS pdf = new PDF2XLS();
            // User
            pdf.setUser(request.getSession().getAttribute("user").toString());
            // File original name
            pdf.setSourceName(Paths.get(part.getSubmittedFileName()).getFileName().toString());
            String fileNameTemp = model.bo.Tools.GenerateString(24);
            // File random source name
            pdf.setSourcePath(HOME_DIRECTORY + "\\" + fileNameTemp + ".pdf");
            // File random name after convert
            pdf.setTargetPath(HOME_DIRECTORY + "\\" + fileNameTemp + ".xlsx");
            // Set status to 0 - pending
            pdf.setResult(0);

            // Write to server disk
            part.write(pdf.getSourcePath());
            // New record to database
            model.bo.Data data = new model.bo.Data();
            data.addStatus(pdf);

            // Return to dashboard
            response.sendRedirect("dashboard");
        }
        catch (Exception ex) {
            System.out.println(ex);
            response.sendRedirect("upload");
        }
    }
}
