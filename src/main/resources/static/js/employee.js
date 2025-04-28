// Sayfa yüklendiğinde
$(document).ready(function() {
    loadActiveOrders();
    loadTableStatus();
});

// Aktif siparişleri yükle
function loadActiveOrders() {
    fetch('/api/orders')
        .then(response => response.json())
        .then(orders => {
            let tableContent = '';
            if (Array.isArray(orders) && orders.length > 0) {
                orders.forEach(order => {
                    tableContent += `
                        <tr>
                            <td>${order.id}</td>
                            <td>${order.tableNumber}</td>
                            <td>${formatOrderItems(order.items)}</td>
                            <td>${formatPrice(order.total)} ₺</td>
                            <td>${formatStatus(order.status)}</td>
                            <td>
                                <button class="btn-small waves-effect waves-light" onclick="updateOrderStatus(${order.id}, 'COMPLETED')">
                                    <i class="material-icons">check</i>
                                </button>
                            </td>
                        </tr>
                    `;
                });
            } else {
                tableContent = '<tr><td colspan="6">Aktif sipariş bulunmamaktadır.</td></tr>';
            }
            $('#activeOrders').html(tableContent);
        })
        .catch(error => {
            console.error('Siparişler yüklenirken hata:', error);
            $('#activeOrders').html('<tr><td colspan="6">Hata: Siparişler yüklenemedi!</td></tr>');
        });
}

// Masa durumunu yükle
function loadTableStatus() {
    fetch('/api/tables')
        .then(response => response.json())
        .then(tables => {
            let content = '';
            if (Array.isArray(tables) && tables.length > 0) {
                tables.forEach(table => {
                    content += `
                        <div class="col s12 m4">
                            <div class="card ${getTableStatusColor(table.status)}">
                                <div class="card-content white-text">
                                    <span class="card-title">Masa ${table.tableNumber}</span>
                                    <p>Kapasite: ${table.capacity} kişi</p>
                                    <p>Durum: ${formatTableStatus(table.status)}</p>
                                </div>
                            </div>
                        </div>
                    `;
                });
            }
            $('#tableStatus').html(content);
        })
        .catch(error => {
            console.error('Masa durumu yüklenirken hata:', error);
            M.toast({html: 'Masa durumu yüklenirken hata oluştu!', classes: 'red'});
        });
}

// Yardımcı fonksiyonlar
function formatOrderItems(items) {
    return items.map(item => `${item.name} x${item.quantity}`).join(', ');
}

function formatPrice(price) {
    return price.toFixed(2);
}

function formatStatus(status) {
    const statusMap = {
        'PENDING': 'Beklemede',
        'IN_PROGRESS': 'Hazırlanıyor',
        'COMPLETED': 'Tamamlandı',
        'CANCELLED': 'İptal Edildi'
    };
    return statusMap[status] || status;
}

function formatTableStatus(status) {
    const statusMap = {
        'EMPTY': 'Boş',
        'OCCUPIED': 'Dolu',
        'RESERVED': 'Rezerve'
    };
    return statusMap[status] || status;
}

function getTableStatusColor(status) {
    const colorMap = {
        'EMPTY': 'green',
        'OCCUPIED': 'red',
        'RESERVED': 'orange'
    };
    return colorMap[status] || 'grey';
}

// Sipariş durumunu güncelle
function updateOrderStatus(orderId, newStatus) {
    fetch(`/api/orders/${orderId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status: newStatus })
    })
    .then(response => {
        if (!response.ok) throw new Error('Network response was not ok');
        loadActiveOrders();
        M.toast({html: 'Sipariş durumu güncellendi!', classes: 'green'});
    })
    .catch(error => {
        console.error('Sipariş durumu güncellenirken hata:', error);
        M.toast({html: 'Sipariş durumu güncellenirken hata oluştu!', classes: 'red'});
    });
}

// Çıkış yap
function logout() {
    window.location.href = 'homepage.html';
} 