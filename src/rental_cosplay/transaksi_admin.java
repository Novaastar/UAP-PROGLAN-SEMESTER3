/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rental_cosplay;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author couser
 */
public class transaksi_admin extends javax.swing.JFrame {
    
    private DefaultTableModel model = null;
    private PreparedStatement stat;
    private ResultSet rs;
    koneksi k = new koneksi();
    
    /**
     * Creates new form transaksi
     */
    public transaksi_admin() {
        initComponents();
        k.connect();
        refreshTable();
        refreshCombo();
    }

    class transaksi extends transaksi_admin {
        int id_transaksi, id_baju, harga, lama_sewa, total_bayar;
        String nama_user, tanggal, kostum, pembayaran;
        
    public transaksi() {
        try {
            // Mengambil data dari field input
            this.nama_user = text_nama_pelanggan.getText();

            // Memproses data dari combo_kostum
            String kostumCombo = combo_kostum.getSelectedItem().toString();
            String[] kostumArr = kostumCombo.split(":");
            this.id_baju = Integer.parseInt(kostumArr[0]);
            this.kostum = kostumArr[1];
            this.harga = Integer.parseInt(kostumArr[2]);

            // Memproses tanggal
            Date date = text_tanggal.getDate();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            this.tanggal = dateFormat.format(date);

            // Memproses lama sewa
            this.lama_sewa = Integer.parseInt(text_lama_sewa.getText());

            // Menghitung total bayar
            this.total_bayar = this.harga * this.lama_sewa;

            // Memproses data dari combo_pembayaran
            String pembayaranCombo = combo_pembayaran.getSelectedItem().toString();
            this.pembayaran = pembayaranCombo; // Menggunakan nilai langsung dari combo box pembayaran
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
            } 
        }
    
    public void updateStatusKostum(int idBaju) {
        try {
            // Query untuk mengubah status kostum menjadi 'Unavailable'
            String query = "UPDATE kostum SET status = 'Unavailable' WHERE id_baju = ? AND status = 'Available'";

            // Menyiapkan statement untuk mengeksekusi query
            PreparedStatement stat = k.getCon().prepareStatement(query);

            // Mengatur parameter ID kostum
            stat.setInt(1, idBaju);

            // Mengeksekusi query untuk memperbarui status kostum
            int rowsUpdated = stat.executeUpdate();

            // Jika ada baris yang diperbarui (berarti kostum berhasil diperbarui statusnya)
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Kostum berhasil disewa dan status diperbarui menjadi Unavailable.");
            } else {
                JOptionPane.showMessageDialog(null, "Kostum tidak tersedia untuk disewa.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    // Menyimpan transaksi dan memperbarui status kostum
public void simpanTransaksi() {
    try {
        // Mendapatkan ID kostum yang dipilih
        int idBaju = Integer.parseInt(combo_kostum.getSelectedItem().toString().split(":")[0]);

        // Cek apakah kostum sudah disewa
        String cekStatusQuery = "SELECT status FROM kostum WHERE id_baju = ?";
        PreparedStatement cekStat = k.getCon().prepareStatement(cekStatusQuery);
        cekStat.setInt(1, idBaju);
        ResultSet rsStatus = cekStat.executeQuery();

        if (rsStatus.next()) {
            String status = rsStatus.getString("status");

            // Jika status tidak 'Available', tampilkan pesan kesalahan
            if (!status.equalsIgnoreCase("Available")) {
                JOptionPane.showMessageDialog(null, "Kostum ini sudah disewa dan tidak tersedia.");
                return;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Kostum dengan ID " + idBaju + " tidak ditemukan.");
            return;
        }

        // Menyiapkan query SQL untuk memasukkan data transaksi ke tabel transaksi
        String query = "INSERT INTO transaksi (nama_user, id_baju, tanggal, kostum, harga, lama_sewa, total_bayar, pembayaran) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stat = k.getCon().prepareStatement(query);

        // Mengambil data dari form
        stat.setString(1, text_nama_pelanggan.getText());
        stat.setInt(2, idBaju);
        stat.setString(3, new SimpleDateFormat("yyyy-MM-dd").format(text_tanggal.getDate()));
        stat.setString(4, combo_kostum.getSelectedItem().toString().split(":")[1]);
        stat.setInt(5, Integer.parseInt(combo_kostum.getSelectedItem().toString().split(":")[2]));
        stat.setInt(6, Integer.parseInt(text_lama_sewa.getText()));
        stat.setInt(7, Integer.parseInt(text_total.getText()));
        stat.setString(8, combo_pembayaran.getSelectedItem().toString());

        // Mengeksekusi query untuk menyimpan transaksi
        stat.executeUpdate();

        // Memperbarui status kostum menjadi 'Unavailable'
        updateStatusKostum(idBaju);

        // Menyegarkan tabel transaksi
        refreshTable();

        // Menampilkan pesan sukses
        JOptionPane.showMessageDialog(null, "Transaksi berhasil disimpan.");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }
}


    // Refresh tabel transaksi
    public void refreshTable() {
        model = new DefaultTableModel();
        model.addColumn("ID Transaksi");
        model.addColumn("Nama user");
        model.addColumn("ID Baju");
        model.addColumn("Tanggal");
        model.addColumn("Nama kostum");
        model.addColumn("Harga");
        model.addColumn("Lama Sewa");
        model.addColumn("Total Bayar");
        model.addColumn("Pembayaran");
        table_transaksi.setModel(model);
        try {
            this.stat = k.getCon().prepareStatement("select * from transaksi");
            this.rs = this.stat.executeQuery();
            while (rs.next()) {
                Object[] data = {
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getString(7),
                    rs.getString(8),
                    rs.getString(9)
                };
                model.addRow(data);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        text_id_transaksi.setText("");
        text_nama_pelanggan.setText("");
        text_lama_sewa.setText("");
        text_total.setText("");
    }

    // Refresh combo box dengan kostum yang tersedia
    public void refreshCombo() {
        try {
            this.stat = k.getCon().prepareStatement("select id_baju, kostum, harga_kostum, status from kostum where status='Available'");
            this.rs = this.stat.executeQuery();
            while (rs.next()) {
                combo_kostum.addItem(rs.getString("id_baju") + ":" + rs.getString("kostum") + ":" + rs.getString("harga_kostum"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    // Action ketika tombol Simpan ditekan
    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        // Memanggil fungsi untuk menyimpan transaksi
        simpanTransaksi();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        text_id_transaksi = new javax.swing.JTextField();
        text_nama_pelanggan = new javax.swing.JTextField();
        combo_kostum = new javax.swing.JComboBox<>();
        text_lama_sewa = new javax.swing.JTextField();
        text_total = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_transaksi = new javax.swing.JTable();
        combo_pembayaran = new javax.swing.JComboBox<>();
        btn_input = new javax.swing.JButton();
        btn_update = new javax.swing.JButton();
        btn_delete = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        text_tanggal = new com.toedter.calendar.JDateChooser();
        btn_kembali = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Transaksi Admin");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        text_id_transaksi.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        text_id_transaksi.setEnabled(false);
        getContentPane().add(text_id_transaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 100, 650, 50));

        text_nama_pelanggan.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        text_nama_pelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_nama_pelangganActionPerformed(evt);
            }
        });
        getContentPane().add(text_nama_pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 170, 650, 50));

        combo_kostum.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        combo_kostum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_kostumActionPerformed(evt);
            }
        });
        getContentPane().add(combo_kostum, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 230, 460, 50));

        text_lama_sewa.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        text_lama_sewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_lama_sewaActionPerformed(evt);
            }
        });
        getContentPane().add(text_lama_sewa, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 360, 660, 50));

        text_total.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        text_total.setEnabled(false);
        getContentPane().add(text_total, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 430, 660, 50));

        table_transaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table_transaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_transaksiMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(table_transaksi);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 660, 850, 390));

        combo_pembayaran.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        combo_pembayaran.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Transfer", "Qris" }));
        combo_pembayaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_pembayaranActionPerformed(evt);
            }
        });
        getContentPane().add(combo_pembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 490, 370, 60));

        btn_input.setText("Input");
        btn_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_inputActionPerformed(evt);
            }
        });
        getContentPane().add(btn_input, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 590, 140, 60));

        btn_update.setText("Update");
        btn_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updateActionPerformed(evt);
            }
        });
        getContentPane().add(btn_update, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 590, 140, 60));

        btn_delete.setText("Delete");
        btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_deleteActionPerformed(evt);
            }
        });
        getContentPane().add(btn_delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 590, 140, 60));

        jButton1.setText("Rekap Laporan");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 590, 150, 60));

        text_tanggal.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        getContentPane().add(text_tanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 290, 660, 50));

        btn_kembali.setText("Kembali");
        btn_kembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_kembaliActionPerformed(evt);
            }
        });
        getContentPane().add(btn_kembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 0, 140, 50));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Transaksi admin [CA069E8].png"))); // NOI18N
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 900, 1070));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void combo_pembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_pembayaranActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_pembayaranActionPerformed

    private void btn_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_inputActionPerformed
        // TODO add your handling code here:
        try {
            transaksi tran = new transaksi();
            text_total.setText(""+tran.total_bayar);
            this.stat = k.getCon().prepareStatement("insert into transaksi values(?,?,?,?,?,?,?,?,?)");
            this.stat.setInt(1, 0);
            this.stat.setString(2, tran.nama_user);
            this.stat.setInt(3, tran.id_baju);
            this.stat.setString(4, tran.tanggal);
            this.stat.setString(5, tran.kostum);
            this.stat.setInt(6, tran.harga);
            this.stat.setInt(7, tran.lama_sewa);
            this.stat.setInt(8, tran.total_bayar);
            this.stat.setString(9, tran.pembayaran);
            int pilihan = JOptionPane.showConfirmDialog(null, 
                    "Tanggal: "+tran.tanggal+
                    "\nNama Pelanggan: "+tran.nama_user+
                    "\nPembelian: "+tran.lama_sewa+" "+tran.kostum+
                    "\nTotal Bayar: "+tran.total_bayar+"\n",
                    "Tambahkan Transkasi?", 
                    JOptionPane.YES_NO_OPTION);
            if (pilihan == JOptionPane.YES_OPTION){
                JOptionPane.showMessageDialog(null, "Data berhasil disimpan!");
                this.stat.executeUpdate();
                refreshTable();
            }else if (pilihan == JOptionPane.NO_OPTION){
                refreshTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }//GEN-LAST:event_btn_inputActionPerformed

    private void text_nama_pelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_nama_pelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_nama_pelangganActionPerformed

    private void btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_deleteActionPerformed
        // TODO add your handling code here:
        try {
        // Pastikan ada baris yang dipilih di tabel
        int selectedRow = table_transaksi.getSelectedRow();
        if (selectedRow != -1) {
            // Ambil ID transaksi dari kolom pertama (ID Transaksi)
            String id_transaksi = table_transaksi.getValueAt(selectedRow, 0).toString();
            
            // Konfirmasi penghapusan data
            int pilihan = JOptionPane.showConfirmDialog(null, 
                    "Apakah Anda yakin ingin menghapus transaksi dengan ID: " + id_transaksi + "?",
                    "Hapus Transaksi", 
                    JOptionPane.YES_NO_OPTION);
            
            if (pilihan == JOptionPane.YES_OPTION) {
                // Query untuk menghapus data
                this.stat = k.getCon().prepareStatement("DELETE FROM transaksi WHERE id_transaksi = ?");
                this.stat.setString(1, id_transaksi);
                this.stat.executeUpdate();
                
                JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
                refreshTable(); // Refresh tabel setelah penghapusan
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silakan pilih transaksi yang akan dihapus.");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage());
    }
    }//GEN-LAST:event_btn_deleteActionPerformed

    private void btn_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_updateActionPerformed
        // TODO add your handling code here:
        try {
            transaksi tran = new transaksi();
            tran.id_transaksi = Integer.parseInt(text_id_transaksi.getText());
            this.stat = k.getCon().prepareStatement("update transaksi set nama_user=?,"
                    + "id_baju=?,tanggal=?,kostum=?,harga=?,lama_sewa=?,total_bayar=?,pembayaran=? "
                    + "where id_transaksi=?");
            this.stat.setString(1, tran.nama_user);
            this.stat.setInt(2, tran.id_baju);
            this.stat.setString(3, tran.tanggal);
            this.stat.setString(4, tran.kostum);
            this.stat.setInt(5, tran.harga);
            this.stat.setInt(6, tran.lama_sewa);
            this.stat.setInt(7, tran.total_bayar);
             this.stat.setString(8, tran.pembayaran);
             this.stat.setInt(9, tran.id_transaksi);
           JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
            this.stat.executeUpdate();
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }//GEN-LAST:event_btn_updateActionPerformed

    private void table_transaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_transaksiMouseClicked
        // TODO add your handling code here:
        text_id_transaksi.setText(model.getValueAt(table_transaksi.getSelectedRow(), 0).toString());
        text_nama_pelanggan.setText(model.getValueAt(table_transaksi.getSelectedRow(), 1).toString());
        text_lama_sewa.setText(model.getValueAt(table_transaksi.getSelectedRow(), 6).toString());
        text_total.setText(model.getValueAt(table_transaksi.getSelectedRow(), 7).toString());
    }//GEN-LAST:event_table_transaksiMouseClicked

    private void combo_kostumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_kostumActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_kostumActionPerformed

    private void btn_kembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_kembaliActionPerformed
        // TODO add your handling code here:
        Menu_admin reg = new Menu_admin();
        reg.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btn_kembaliActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        try {
            File namafile = new File("src/laporan/laporan_transaksi.jasper");
            JasperPrint jp = JasperFillManager.fillReport(namafile.getPath(), null, k.getCon());
            JasperViewer.viewReport(jp,false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void text_lama_sewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_lama_sewaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_lama_sewaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(transaksi_admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(transaksi_admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(transaksi_admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(transaksi_admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new transaksi_admin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_delete;
    private javax.swing.JButton btn_input;
    private javax.swing.JButton btn_kembali;
    private javax.swing.JButton btn_update;
    private javax.swing.JComboBox<String> combo_kostum;
    private javax.swing.JComboBox<String> combo_pembayaran;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table_transaksi;
    private javax.swing.JTextField text_id_transaksi;
    private javax.swing.JTextField text_lama_sewa;
    private javax.swing.JTextField text_nama_pelanggan;
    private com.toedter.calendar.JDateChooser text_tanggal;
    private javax.swing.JTextField text_total;
    // End of variables declaration//GEN-END:variables
}
