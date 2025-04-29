// Sayfa yüklendiğinde masaları yükle
document.addEventListener('DOMContentLoaded', function() {
    loadTables();
    var elems = document.querySelectorAll('select');
    M.FormSelect.init(elems);
});

// Masaları yükle
function loadTables() {
    fetch('/api/tables')
        .then(response => response.json())
        .then(tables => {
            const tableSelect = document.getElementById('tableSelect');
            tableSelect.innerHTML = '<option value="" disabled selected>Masa Seçiniz</option>';
            tables.forEach(table => {
                tableSelect.innerHTML += `<option value="${table.id}">Masa ${table.tableNumber}</option>`;
            });
            M.FormSelect.init(tableSelect);
        })
        .catch(error => {
            console.error('Masalar yüklenirken hata:', error);
            M.toast({html: 'Masalar yüklenirken bir hata oluştu!'});
        });
}

// Siparişi gönder
function submitOrder() {
    const tableId = document.getElementById('tableSelect').value;
    const customerName = document.getElementById('customerName').value;
    
    if (!tableId) {
        M.toast({html: 'Lütfen bir masa seçin!'});
        return;
    }
    
    if (!customerName) {
        M.toast({html: 'Lütfen müşteri adı girin!'});
        return;
    }

    // Sepetteki ürünleri al
    const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
    if (cartItems.length === 0) {
        M.toast({html: 'Sepetiniz boş!'});
        return;
    }

    // Sipariş nesnesini oluştur
    const order = {
        customerName: customerName,
        orderTime: new Date(),
        totalPrice: calculateTotal(),
        table: { id: parseInt(tableId) },
        orderItems: cartItems.map(item => ({
            itemName: item.name,
            price: item.price,
            quantity: item.quantity
        }))
    };

    // Siparişi gönder
    fetch('/api/orders', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(order)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => {
                throw new Error(err.error || 'Sipariş oluşturulurken bir hata oluştu!');
            });
        }
        return response.json();
    })
    .then(data => {
        M.toast({html: 'Siparişiniz başarıyla alındı!'});
        // Sepeti temizle
        localStorage.removeItem('cart');
        updateCartCount();
        // Modalı kapat
        var modal = M.Modal.getInstance(document.getElementById('orderForm'));
        modal.close();
    })
    .catch(error => {
        console.error('Sipariş hatası:', error);
        M.toast({html: error.message});
    });
}

// Toplam tutarı hesapla
function calculateTotal() {
    const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
    return cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
} 