package com.zearoconsulting.zearopos.presentation.pdf;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.zearoconsulting.zearopos.AndroidApplication;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.utils.Common;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by saravanan on 17-08-2016.
 */
public class InvoiceReport {

    Context mContext;
    File myDir;
    String FILE = Environment.getExternalStorageDirectory().toString()
            + "/PDF/" + "Invoice.pdf";
    String root = Environment.getExternalStorageDirectory().toString();
    String myFile = "/PDF/Invoice.pdf";
    String my_Path = Environment.getExternalStorageDirectory() + myFile;
    Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD
            | Font.UNDERLINE, BaseColor.GRAY);
    Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
    private long posId = 0;
    private List<POSLineItem> lineItem;
    private double totalAmount, paidAmount, returnAmount;

    public InvoiceReport() {
    }

    public InvoiceReport(Context context,long posid, List<POSLineItem> lineItems, double totalAmt, double paidAmt, double returnAmt) {

        this.mContext = context;
        this.posId = posid;
        this.lineItem = lineItems;
        this.totalAmount = totalAmt;
        this.paidAmount = paidAmt;
        this.returnAmount = returnAmt;

        // Create Directory in External Storage
        myDir = new File(root + "/PDF");
        if (!myDir.exists()) {
            myDir.mkdir();
            Log.i("PDF Directory", "Pdf Directory created");
        }
    }

    public static PdfPCell getNormalCell(String string, int size)
            throws DocumentException, IOException {
        if (string != null && "".equals(string)) {
            return new PdfPCell();
        }
        Font f = new Font(Font.FontFamily.TIMES_ROMAN, size, Font.BOLD);
        if (size < 0) {
            f.setColor(BaseColor.RED);
            size = -size;
        }
        f.setSize(size);
        PdfPCell cell = new PdfPCell(new Phrase(string, f));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }

    public void createPdf() {

        // Create New Blank Document
        Document document = new Document(PageSize.A6);

        // Create Pdf Writer for Writting into New Created Document
        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));

            // Open Document for Writting into document
            document.open();

            // User Define Method
            addMetaData(document);
            addTitlePage(document);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Close Document after writting all content
        document.close();
    }

    // Set PDF document Properties
    public void addMetaData(Document document)

    {
        document.addTitle("INVOICE");
        document.addSubject("Invoice Info");
        document.addKeywords("Order details");
        document.addAuthor("Zearo");
        document.addCreator("Zearo");
    }

    public void addTitlePage(Document document) throws DocumentException, IOException {
        // Font Style for Document

        Drawable d = mContext.getResources().getDrawable(R.drawable.coffeelogo);

        BitmapDrawable bitDw = ((BitmapDrawable) d);
        Bitmap bmp = bitDw.getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image image = Image.getInstance(stream.toByteArray());
        image.setAlignment(Image.ALIGN_CENTER);
        document.add(image);

        // Start New Paragraph
        Paragraph prHead = new Paragraph();
        // Set Font in this Paragraph
        prHead.setFont(titleFont);
        // Add item into Paragraph
        prHead.add("Your order number is\n");

        // Create Table into Document with 1 Row
        PdfPTable myTable = new PdfPTable(1);
        // 100.0f mean width of table is same as Document size
        myTable.setWidthPercentage(100.0f);

        // Create New Cell into Table
        PdfPCell myCell = new PdfPCell(new Paragraph(""));
        myCell.setBorder(Rectangle.BOTTOM);

        // Add Cell into Table
        myTable.addCell(myCell);

        prHead.setFont(catFont);
        prHead.add("\n" + posId);
        prHead.setAlignment(Element.ALIGN_CENTER);

        prHead.add("\n\n");

        // Add all above details into Document
        document.add(prHead);

        DottedLineSeparator customLine = new DottedLineSeparator();
        customLine.setGap(7);
        customLine.setLineWidth(3);
        document.add(customLine);
        document.add(Chunk.NEWLINE);

        // Now Start another New Paragraph
        Paragraph prOrgInfo = new Paragraph();
        prOrgInfo.setFont(smallBold);
        prOrgInfo.add("Lounge\n");
        prOrgInfo.add("For Delivery, call: 44442504 or\n");
        prOrgInfo.add("www.chocolatecoffee.net\n");
        prOrgInfo.setAlignment(Element.ALIGN_CENTER);
        document.add(prOrgInfo);

        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yy hh:mm:ss");
        dt.format(new Date());

        document.add(new Paragraph("ORD #"+posId+" "+dt.format(new Date())+"\n"));

        //list all the products sold to the customer
        float[] columnWidths = {20.0f, 50.0f, 30.0f};
        //create PDF table with the given widths
        PdfPTable table = new PdfPTable(columnWidths);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        // set table width a percentage of the page width
        table.setTotalWidth(100.0f);

        PdfPCell cell = new PdfPCell(getNormalCell("QTY", 12));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(getNormalCell("ITEM", 12));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(getNormalCell("TOTAL", 12));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
        table.setHeaderRows(1);

        for (int i = 0; i < lineItem.size(); i++) {
            table.addCell("" + lineItem.get(i).getPosQty());
            table.addCell(lineItem.get(i).getProductName());
            table.addCell("" + Common.valueFormatter(lineItem.get(i).getTotalPrice()));
        }

        //absolute location to print the PDF table from
        document.add(table);

        document.add(new Paragraph("\n"));

        //list all the products sold to the customer
        float[] footColumnWidths = {50.0f, 50.0f};
        //create PDF table with the given widths
        PdfPTable footTable = new PdfPTable(2);
        footTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        // set table width a percentage of the page width
        footTable.setTotalWidth(100.0f);
        PdfPCell fooCell;
        for(int i=0;i<3 ; i++){

            if(i == 0){
                fooCell = new PdfPCell(getNormalCell("Subtotal", 12));
                fooCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                fooCell.setBorder(Rectangle.NO_BORDER);
                footTable.addCell(fooCell);

                fooCell = new PdfPCell(getNormalCell("" + Common.valueFormatter(totalAmount), 12));
                fooCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                fooCell.setBorder(Rectangle.NO_BORDER);
                footTable.addCell(fooCell);
            }else if(i == 1){
                fooCell = new PdfPCell(getNormalCell("Cash Tendered", 12));
                fooCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                fooCell.setBorder(Rectangle.NO_BORDER);
                footTable.addCell(fooCell);

                fooCell = new PdfPCell(getNormalCell("" + Common.valueFormatter(paidAmount), 12));
                fooCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                fooCell.setBorder(Rectangle.NO_BORDER);
                footTable.addCell(fooCell);
            }else if(i == 2){
                fooCell = new PdfPCell(getNormalCell("Change", 12));
                fooCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                fooCell.setBorder(Rectangle.NO_BORDER);
                footTable.addCell(fooCell);

                fooCell = new PdfPCell(getNormalCell("" + Common.valueFormatter(returnAmount), 12));
                fooCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                fooCell.setBorder(Rectangle.NO_BORDER);
                footTable.addCell(fooCell);
            }
        }

        document.add(footTable);

        // Now Start another New Paragraph
        Paragraph prOrgAddressInfo = new Paragraph();
        prOrgAddressInfo.setFont(smallBold);
        prOrgAddressInfo.add("\nThe Pearl - Qatar, Media Central, Bldg No: 4 Unit: 66\n");
        prOrgAddressInfo.add("Phone: +974 44442504\n");
        prOrgAddressInfo.add("email: info@chocolatecoffee.net\n");
        prOrgAddressInfo.add("Thank You For Choosing Our Shop\n");
        prOrgAddressInfo.setAlignment(Element.ALIGN_CENTER);
        document.add(prOrgAddressInfo);

        // Create new Page in PDF
        document.newPage();
    }

    public void viewPdf(Context context) {
        mContext = context;
        File file = myDir = new File(my_Path);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
        if (activities.size() > 0) {
            mContext.startActivity(intent);
        } else {
            // Do something else here. Maybe pop up a Dialog or Toast
        }

    }

   /* public static Font getFontForThisLanguage(String language) {
        if ("czech".equals(language)) {
            return FontFactory.getFont(catf, "Cp1250", true);
        }
        if ("greek".equals(language)) {
            return FontFactory.getFont(FONT, "Cp1253", true);
        }
        return FontFactory.getFont(FONT, null, true);
    }*/
}
