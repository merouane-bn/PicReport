package com.example.myapplication.user;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;

public class DownloadPdf extends DialogFragment {

    private static final String PDF_URL_KEY = "pdfUrl";

    public static DownloadPdf newInstance(String pdfUrl) {
        DownloadPdf fragment = new DownloadPdf();
        Bundle args = new Bundle();
        args.putString(PDF_URL_KEY, pdfUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_download_pdf, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view)
                .setCancelable(false);

        // Get PDF URL from arguments
        String pdfUrl = getArguments().getString(PDF_URL_KEY);

        Button buttonDownload = view.findViewById(R.id.buttonDownload);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);

        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Download PDF
                downloadPdf(pdfUrl);
                dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();
    }

    private void downloadPdf(String pdfUrl) {
        // Implement PDF download logic here
        // For example, you can use Intent to download the PDF
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl));
        startActivity(intent);
    }
}
