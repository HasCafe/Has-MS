// Global fonksiyonlar
window.loadCategories = function() {
	console.log('Kategoriler yükleniyor...');
	
	// Yükleniyor mesajını göster
	$('#categoryList').html('<tr><td colspan="3">Yükleniyor...</td></tr>');
	
	fetch('/api/categories')
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok: ' + response.status);
			}
			return response.json();
		})
		.then(categories => {
			console.log('Sunucudan gelen kategoriler:', categories);
			
			var categoryList = $('#categoryList');
			categoryList.empty();
			
			// Ürün ekleme ve görüntüleme için select elementlerini güncelle
			var productCategory = $('#productCategory');
			var viewCategory = $('#viewCategory');
			if (productCategory.length) productCategory.empty();
			if (viewCategory.length) viewCategory.empty();
			
			if (productCategory.length) productCategory.append('<option value="" disabled selected>Kategori Seçin</option>');
			if (viewCategory.length) viewCategory.append('<option value="" disabled selected>Kategori Seçin</option>');
			
			if (Array.isArray(categories) && categories.length > 0) {
				categories.forEach(function(category) {
					console.log('İşlenen kategori:', category);
					categoryList.append(`
						<tr>
							<td>${category.id || 'N/A'}</td>
							<td>${category.name || 'İsimsiz'}</td>
							<td>
								<button class="btn red waves-effect waves-light" onclick="deleteCategory(${category.id})">
									<i class="material-icons">delete</i>
								</button>
							</td>
						</tr>
					`);
					
					// Select elementleri için
					if (productCategory.length) {
						productCategory.append(`<option value="${category.id}">${category.name}</option>`);
					}
					if (viewCategory.length) {
						viewCategory.append(`<option value="${category.id}">${category.name}</option>`);
					}
				});
				
				// Select elementlerini yenile
				$('select').formSelect();
			} else {
				categoryList.html('<tr><td colspan="3">Henüz kategori bulunmamaktadır.</td></tr>');
			}
		})
		.catch(error => {
			console.error('Kategoriler yüklenirken hata:', error);
			$('#categoryList').html('<tr><td colspan="3">Hata: Kategoriler yüklenemedi!</td></tr>');
			M.toast({html: 'Kategoriler yüklenirken bir hata oluştu! Hata: ' + error.message, classes: 'red'});
		});
}

window.addCategory = function() {
	const categoryName = $('#categoryName').val().trim();
	
	if (!categoryName) {
		M.toast({html: 'Lütfen kategori adını giriniz!'});
		return;
	}

	console.log('Kategori ekleniyor:', categoryName);
	
	fetch('/api/categories', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify({ name: categoryName })
	})
	.then(response => {
		if (!response.ok) {
			throw new Error('Network response was not ok');
		}
		return response.json();
	})
	.then(data => {
		console.log('Kategori eklendi:', data);
		$('#categoryName').val('');
		M.updateTextFields(); // Materialize labellarını güncelle
		M.toast({html: 'Kategori başarıyla eklendi!'});
		loadCategories();
	})
	.catch(error => {
		console.error('Kategori eklenirken hata:', error);
		M.toast({html: 'Kategori eklenirken bir hata oluştu!'});
	});
}

window.deleteCategory = function(id) {
	if (!id) {
		console.error('Geçersiz kategori ID\'si');
		return;
	}

	if (confirm('Bu kategoriyi ve içindeki tüm ürünleri silmek istediğinizden emin misiniz?')) {
		console.log('Kategori silme işlemi başlatılıyor, ID:', id);

		// Önce kategoriye ait ürünleri sil
		fetch(`/api/categories/${id}/products`, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json'
			}
		})
		.then(response => {
			if (!response.ok) {
				throw new Error('Ürünler alınırken hata oluştu');
			}
			return response.json();
		})
		.then(products => {
			console.log('Silinecek ürünler:', products);
			
			// Her ürünü tek tek sil
			const deletePromises = products.map(product => 
				fetch(`/api/products/${product.id}`, {
					method: 'DELETE',
					headers: {
						'Content-Type': 'application/json'
					}
				}).then(response => {
					if (!response.ok) {
						throw new Error(`Ürün silinirken hata: ${product.id}`);
					}
				})
			);
			
			// Tüm ürünler silindikten sonra kategoriyi sil
			return Promise.all(deletePromises).then(() => {
				return fetch(`/api/categories/${id}`, {
					method: 'DELETE',
					headers: {
						'Content-Type': 'application/json'
					}
				});
			});
		})
		.then(response => {
			if (!response.ok) {
				throw new Error('Kategori silinirken hata oluştu');
			}
			console.log('Kategori ve ürünleri başarıyla silindi, ID:', id);
			M.toast({html: 'Kategori ve içindeki ürünler başarıyla silindi!', classes: 'green'});
			loadCategories();
			// Sadece ana sayfadaysa menuCategories'i yükle
			if (window.location.pathname.includes('index.html') || window.location.pathname === '/') {
				loadMenuCategories();
			}
		})
		.catch(error => {
			console.error('Kategori silme hatası:', error);
			M.toast({html: 'Kategori silinirken bir hata oluştu!', classes: 'red'});
		});
	}
}

// Çalışan fonksiyonları
function loadEmployees() {
	console.log('Çalışanlar yükleniyor...');
	fetch('/api/employees')
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.json();
		})
		.then(employees => {
			const employeeList = $('#employeeList');
			employeeList.empty();
			
			if (Array.isArray(employees) && employees.length > 0) {
				employees.forEach(employee => {
					employeeList.append(`
						<tr>
							<td>${employee.name || 'İsimsiz'}</td>
							<td>${employee.username}</td>
							<td>
								<button class="btn red waves-effect waves-light" onclick="deleteEmployee(${employee.id})">
									<i class="material-icons">delete</i>
								</button>
							</td>
						</tr>
					`);
				});
			} else {
				employeeList.html('<tr><td colspan="3">Henüz çalışan bulunmamaktadır.</td></tr>');
			}
		})
		.catch(error => {
			console.error('Çalışanlar yüklenirken hata:', error);
			$('#employeeList').html('<tr><td colspan="3">Hata: Çalışanlar yüklenemedi!</td></tr>');
			M.toast({html: 'Çalışanlar yüklenirken bir hata oluştu!', classes: 'red'});
		});
}

function addEmployee() {
	const name = $('#employeeName').val().trim();
	const username = $('#employeeUsername').val().trim();
	const password = $('#employeePassword').val().trim();
	
	if (!name || !username || !password) {
		M.toast({html: 'Lütfen tüm alanları doldurunuz!', classes: 'red'});
		return;
	}
	
	fetch('/api/employees', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify({
			name: name,
			username: username,
			password: password
		})
	})
	.then(response => {
		if (!response.ok) {
			throw new Error('Network response was not ok');
		}
		return response.json();
	})
	.then(data => {
		$('#employeeName').val('');
		$('#employeeUsername').val('');
		$('#employeePassword').val('');
		M.updateTextFields();
		M.toast({html: 'Çalışan başarıyla eklendi!', classes: 'green'});
		loadEmployees();
	})
	.catch(error => {
		console.error('Çalışan eklenirken hata:', error);
		M.toast({html: 'Çalışan eklenirken bir hata oluştu!', classes: 'red'});
	});
}

function deleteEmployee(id) {
	if (!id) {
		console.error('Geçersiz çalışan ID\'si');
		return;
	}
	
	if (confirm('Bu çalışanı silmek istediğinizden emin misiniz?')) {
		fetch(`/api/employees/${id}`, {
			method: 'DELETE'
		})
		.then(response => {
			if (!response.ok) throw new Error('Network response was not ok');
			M.toast({html: 'Çalışan başarıyla silindi!', classes: 'green'});
			loadEmployees();
		})
		.catch(error => {
			console.error('Çalışan silinirken hata:', error);
			M.toast({html: 'Çalışan silinirken bir hata oluştu!', classes: 'red'});
		});
	}
}

// Masa fonksiyonları
function loadTables() {
	console.log('Masalar yükleniyor...');
	
	fetch('/api/tables')
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.json();
		})
		.then(tables => {
			console.log('Yüklenen masalar:', tables);
			const tableList = $('#tableList');
			tableList.empty();
			
			if (Array.isArray(tables) && tables.length > 0) {
				tables.forEach(table => {
					tableList.append(`
						<tr>
							<td>${table.tableNumber}</td>
							<td>${table.capacity}</td>
							<td>
								<button class="btn red waves-effect waves-light" onclick="deleteTable(${table.id})">
									<i class="material-icons">delete</i>
								</button>
							</td>
						</tr>
					`);
				});
			} else {
				tableList.html('<tr><td colspan="3">Henüz masa bulunmamaktadır.</td></tr>');
			}
		})
		.catch(error => {
			console.error('Masalar yüklenirken hata:', error);
			$('#tableList').html('<tr><td colspan="3" class="red-text">Masalar yüklenirken bir hata oluştu!</td></tr>');
			M.toast({html: 'Masalar yüklenirken bir hata oluştu!', classes: 'red'});
		});
}

function addTable() {
	const tableNumber = parseInt($('#tableNumber').val().trim());
	const capacity = parseInt($('#tableCapacity').val().trim());
	
	if (isNaN(tableNumber) || isNaN(capacity)) {
		M.toast({html: 'Lütfen geçerli bir masa numarası ve kapasite giriniz!', classes: 'red'});
		return;
	}
	
	fetch('/api/tables', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify({
			tableNumber: tableNumber,
			capacity: capacity
		})
	})
	.then(response => {
		if (!response.ok) {
			throw new Error('Network response was not ok');
		}
		return response.json();
	})
	.then(data => {
		console.log('Masa eklendi:', data);
		$('#tableNumber').val('');
		$('#tableCapacity').val('');
		M.updateTextFields();
		M.toast({html: 'Masa başarıyla eklendi!', classes: 'green'});
		loadTables();
	})
	.catch(error => {
		console.error('Masa eklenirken hata:', error);
		M.toast({html: 'Masa eklenirken bir hata oluştu!', classes: 'red'});
	});
}

function deleteTable(id) {
	if (!id) {
		console.error('Geçersiz masa ID\'si');
		return;
	}
	
	if (confirm('Bu masayı silmek istediğinizden emin misiniz?')) {
		fetch(`/api/tables/${id}`, {
			method: 'DELETE'
		})
		.then(response => {
			if (!response.ok) throw new Error('Network response was not ok');
			M.toast({html: 'Masa başarıyla silindi!', classes: 'green'});
			loadTables();
		})
		.catch(error => {
			console.error('Masa silinirken hata:', error);
			M.toast({html: 'Masa silinirken bir hata oluştu!', classes: 'red'});
		});
	}
}

// Ürün fonksiyonları
function loadProducts(categoryId) {
	console.log('Ürünler yükleniyor, kategori:', categoryId);
	const productList = $('#productList');
	
	if (!productList.length) {
		console.error('productList elementi bulunamadı');
		return;
	}
	
	productList.html('<tr><td colspan="3">Yükleniyor...</td></tr>');
	
	let url = categoryId ? `/api/categories/${categoryId}/products` : '/api/products';
	
	fetch(url)
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.json();
		})
		.then(products => {
			productList.empty();
			
			if (Array.isArray(products) && products.length > 0) {
				products.forEach(product => {
					productList.append(`
						<tr>
							<td>${product.name}</td>
							<td>₺${product.price.toFixed(2)}</td>
							<td>
								<button class="btn red waves-effect waves-light" onclick="deleteProduct(${product.id})">
									<i class="material-icons">delete</i>
								</button>
							</td>
						</tr>
					`);
				});
			} else {
				productList.html('<tr><td colspan="3">Bu kategoride ürün bulunmamaktadır.</td></tr>');
			}
		})
		.catch(error => {
			console.error('Ürünler yüklenirken hata:', error);
			productList.html('<tr><td colspan="3">Hata: Ürünler yüklenemedi!</td></tr>');
			M.toast({html: 'Ürünler yüklenirken bir hata oluştu!', classes: 'red'});
		});
}

function addProduct() {
	const categoryId = $('#productCategory').val();
	const name = $('#productName').val().trim();
	const price = parseFloat($('#productPrice').val().trim());
	const description = $('#productDescription').val().trim();
	const imageFile = $('#productImageFile')[0].files[0];
	
	if (!categoryId || !name || isNaN(price)) {
		M.toast({html: 'Lütfen gerekli alanları doldurunuz!', classes: 'red'});
		return;
	}

	console.log('Ürün ekleniyor:', {
		categoryId: categoryId,
		name: name,
		price: price,
		description: description
	});

	const formData = new FormData();
	formData.append('name', name);
	formData.append('price', price);
	if (description) {
		formData.append('description', description);
	}
	if (imageFile) {
		formData.append('image', imageFile);
	}

	fetch(`/api/categories/${categoryId}/products`, {
		method: 'POST',
		body: formData
	})
	.then(response => {
		if (!response.ok) {
			throw new Error('Network response was not ok');
		}
		return response.json();
	})
	.then(data => {
		console.log('Ürün başarıyla eklendi:', data);
		$('#productName').val('');
		$('#productPrice').val('');
		$('#productDescription').val('');
		$('#productImageFile').val('');
		$('.file-path').val('');
		M.updateTextFields();
		M.toast({html: 'Ürün başarıyla eklendi!', classes: 'green'});
		
		// Ürün listesini güncelle
		const selectedCategory = $('#viewCategory').val();
		if (selectedCategory) {
			loadProducts(selectedCategory);
		}
	})
	.catch(error => {
		console.error('Ürün eklenirken hata:', error);
		M.toast({html: 'Ürün eklenirken bir hata oluştu!', classes: 'red'});
	});
}

function deleteProduct(id) {
	if (!id) {
		console.error('Geçersiz ürün ID\'si');
		return;
	}
	
	if (confirm('Bu ürünü silmek istediğinizden emin misiniz?')) {
		fetch(`/api/products/${id}`, {
			method: 'DELETE'
		})
		.then(response => {
			if (!response.ok) throw new Error('Network response was not ok');
			M.toast({html: 'Ürün başarıyla silindi!', classes: 'green'});
			loadProducts($('#viewCategory').val());
		})
		.catch(error => {
			console.error('Ürün silinirken hata:', error);
			M.toast({html: 'Ürün silinirken bir hata oluştu!', classes: 'red'});
		});
	}
}

// Sayfa yüklendiğinde
$(document).ready(function() {
	console.log('Sayfa yükleniyor...');
	
	// Initialize Materialize components
	$('.modal').modal();
	$('select').formSelect();
	
	// Initialize sidenav
	$('.sidenav').sidenav({
		edge: 'left',
		draggable: true,
		preventScrolling: true
	});

	// Hide admin panel initially
	$('#adminPanel').hide();
	
	// Prevent form submission
	$('form').on('submit', function(e) {
		e.preventDefault();
	});

	// Initialize tabs
	$('.tabs').tabs({
		swipeable: false,
		onShow: function(tab) {
			const categoryId = $(tab).attr('data-category-id');
			if (categoryId) {
				loadProducts(categoryId);
			}
		}
	});

	// Load initial data if on admin panel
	if (window.location.pathname.includes('admin.html')) {
		loadAllData();
	}
	
	// Load menu categories if on index page
	if (window.location.pathname.includes('index.html') || window.location.pathname === '/') {
		loadMenuCategories();
	}
	
	// Check if employee is logged in on employee panel
	if (window.location.pathname.includes('employee-panel.html')) {
		const employee = localStorage.getItem('employee');
		if (!employee) {
			window.location.href = 'index.html';
		} else {
			$('#loginForm').hide();
			$('#employeePanel').show();
			loadEmployeePanelData();
		}
	}
});

// Çalışan girişi
function employeeLogin() {
	const username = $('#username').val();
	const password = $('#password').val();
	
	if (!username || !password) {
		M.toast({html: 'Lütfen kullanıcı adı ve şifre giriniz!', classes: 'red'});
		return;
	}
	
	fetch('/api/employees')
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.json();
		})
		.then(employees => {
			const employee = employees.find(e => 
				e.username === username && e.password === password
			);
			
			if (employee) {
				localStorage.setItem('employee', JSON.stringify(employee));
				localStorage.setItem('lastActivity', new Date().getTime());
				window.location.href = 'employee-panel.html';
			} else {
				M.toast({html: 'Hatalı kullanıcı adı veya şifre!', classes: 'red'});
			}
		})
		.catch(error => {
			console.error('Giriş hatası:', error);
			M.toast({html: 'Giriş yapılırken bir hata oluştu!', classes: 'red'});
		});
}

// Çalışan çıkışı
function employeeLogout() {
	localStorage.removeItem('employee');
	localStorage.removeItem('lastActivity');
	window.location.href = 'index.html';
}

// Çalışan paneli verilerini yükle
function loadEmployeePanelData() {
	loadTables();
}

// Tüm verileri yükle
function loadAllData() {
	loadCategories();
	loadEmployees();
	loadTables();
	initializeSelects();
}

// Initialize select elements
function initializeSelects() {
	// Initialize category selects for product management
	$.get('/api/categories', function(categories) {
		let options = '<option value="" disabled selected>Kategori Seçin</option>';
		categories.forEach(category => {
			options += `<option value="${category.id}">${category.name}</option>`;
		});
		$('#productCategory, #viewCategory').html(options);
		$('select').formSelect(); // Reinitialize Materialize selects
	}).fail(function(error) {
		M.toast({html: 'Kategoriler yüklenirken hata oluştu!', classes: 'red'});
		console.error('Error loading categories:', error);
	});
}

// Menü kategorilerini yükle
function loadMenuCategories() {
	console.log('Menü kategorileri yükleniyor...');
	
	fetch('/api/categories')
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.json();
		})
		.then(categories => {
			console.log('Yüklenen kategoriler:', categories);
			
			// DOM elementlerini seç
			const categoryTabs = document.getElementById('categoryTabs');
			const productList = document.getElementById('productList');
			
			if (!categoryTabs || !productList) {
				console.error('Gerekli elementler bulunamadı');
				return;
			}
			
			// Mevcut tabs instance'ı varsa yok et
			const tabsInstance = M.Tabs.getInstance(categoryTabs);
			if (tabsInstance) {
				tabsInstance.destroy();
			}
			
			// Kategorileri temizle
			categoryTabs.innerHTML = '';
			productList.innerHTML = '';
			
			// Tüm ürünler sekmesi
			categoryTabs.innerHTML += `
				<li class="tab col s3">
					<a class="active" href="#all-products">Tüm Ürünler</a>
				</li>
			`;
			
			// Tüm ürünler container'ı
			const allProductsDiv = document.createElement('div');
			allProductsDiv.id = 'all-products';
			allProductsDiv.className = 'col s12';
			productList.appendChild(allProductsDiv);
			
			// Kategorileri ekle
			if (Array.isArray(categories) && categories.length > 0) {
				categories.forEach(category => {
					// Kategori tab'ı
					categoryTabs.innerHTML += `
						<li class="tab col s3">
							<a href="#category-${category.id}">${category.name}</a>
						</li>
					`;
					
					// Kategori container'ı
					const categoryDiv = document.createElement('div');
					categoryDiv.id = `category-${category.id}`;
					categoryDiv.className = 'col s12';
					productList.appendChild(categoryDiv);
				});
			}
			
			// Tabs'ı başlat
			const options = {
				onShow: (content) => {
					const categoryId = content.id.replace('category-', '');
					if (categoryId === 'all-products') {
						loadAllProducts();
					} else {
						loadProductsByCategory(categoryId);
					}
				}
			};
			
			// Tabs'ı başlat ve instance'ı sakla
			M.Tabs.init(categoryTabs, options);
			
			// İlk yüklemede tüm ürünleri göster
			loadAllProducts();
		})
		.catch(error => {
			console.error('Kategoriler yüklenirken hata:', error);
			const categoryTabs = document.getElementById('categoryTabs');
			const productList = document.getElementById('productList');
			
			if (categoryTabs) {
				categoryTabs.innerHTML = `
					<li class="tab col s12">
						<a href="#error">Hata</a>
					</li>
				`;
			}
			
			if (productList) {
				productList.innerHTML = `
					<div id="error" class="col s12">
						<p class="center-align red-text">Kategoriler yüklenirken bir hata oluştu!</p>
					</div>
				`;
			}
			
			// Hata durumunda da tabs'ı başlat
			if (categoryTabs) {
				M.Tabs.init(categoryTabs);
			}
			
			M.toast({html: 'Kategoriler yüklenirken bir hata oluştu!', classes: 'red'});
		});
}

function loadAllProducts() {
	console.log('Tüm ürünler yükleniyor...');
	const allProductsDiv = $('#all-products');
	
	if (!allProductsDiv.length) {
		console.error('all-products elementi bulunamadı');
		return;
	}
	
	allProductsDiv.html('<p class="center-align">Ürünler yükleniyor...</p>');
	
	fetch('/api/products')
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.json();
		})
		.then(products => {
			console.log('Yüklenen tüm ürünler:', products);
			displayProducts(products, allProductsDiv);
		})
		.catch(error => {
			console.error('Ürünler yüklenirken hata:', error);
			allProductsDiv.html('<p class="center-align red-text">Ürünler yüklenirken bir hata oluştu!</p>');
			M.toast({html: 'Ürünler yüklenirken bir hata oluştu!', classes: 'red'});
		});
}

function loadProductsByCategory(categoryId) {
	console.log(`${categoryId} kategorisinin ürünleri yükleniyor...`);
	const categoryDiv = $(`#category-${categoryId}`);
	
	if (!categoryDiv.length) {
		console.error(`category-${categoryId} elementi bulunamadı`);
		return;
	}
	
	categoryDiv.html('<p class="center-align">Ürünler yükleniyor...</p>');
	
	fetch(`/api/categories/${categoryId}/products`)
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.json();
		})
		.then(products => {
			console.log(`Kategori ${categoryId} için yüklenen ürünler:`, products);
			displayProducts(products, categoryDiv);
		})
		.catch(error => {
			console.error(`Kategori ${categoryId} ürünleri yüklenirken hata:`, error);
			categoryDiv.html('<p class="center-align red-text">Ürünler yüklenirken bir hata oluştu!</p>');
			M.toast({html: 'Ürünler yüklenirken bir hata oluştu!', classes: 'red'});
		});
}

// Ürünleri görüntüle
function displayProducts(products, container) {
	if (!Array.isArray(products) || products.length === 0) {
		container.html('<p class="center-align">Bu kategoride henüz ürün bulunmamaktadır.</p>');
		return;
	}

	const productsHtml = products.map(product => `
		<div class="col s12 m6 l4">
			<div class="card">
				<div class="card-image">
					<img src="${product.imageUrl || 'resimler/default-product.jpg'}" alt="${product.name}">
				</div>
				<div class="card-content">
					<span class="card-title">${product.name}</span>
					<p>${product.description || ''}</p>
					<p class="price">${product.price} ₺</p>
				</div>
				<div class="card-action">
					<button class="btn waves-effect waves-light" onclick="addToCart(${product.id})">
						<i class="material-icons left">add_shopping_cart</i>
						Sepete Ekle
					</button>
				</div>
			</div>
		</div>
	`).join('');

	container.html(productsHtml);
}

// Sepet işlemleri için global değişkenler
let cartItems = [];

// Sepete ürün ekle
function addToCart(productId) {
	fetch(`/api/products/${productId}`)
		.then(response => {
			if (!response.ok) {
				throw new Error('Ürün bulunamadı');
			}
			return response.json();
		})
		.then(product => {
			// LocalStorage'dan mevcut sepeti al
			let cartItems = JSON.parse(localStorage.getItem('cart')) || [];
			
			// Ürün zaten sepette var mı kontrol et
			const existingItem = cartItems.find(item => item.id === product.id);
			
			if (existingItem) {
				existingItem.quantity += 1;
			} else {
				cartItems.push({
					id: product.id,
					name: product.name,
					price: product.price,
					quantity: 1,
					imageUrl: product.imageUrl || 'resimler/default-product.jpg'
				});
			}
			
			// Sepeti localStorage'a kaydet
			localStorage.setItem('cart', JSON.stringify(cartItems));
			
			updateCartDisplay();
			M.toast({html: 'Ürün sepete eklendi!', classes: 'rounded green'});
		})
		.catch(error => {
			console.error('Ürün eklenirken hata:', error);
			M.toast({html: 'Ürün eklenirken bir hata oluştu!', classes: 'rounded red'});
		});
}

// Sepetten ürün çıkar
function removeFromCart(productId) {
	let cartItems = JSON.parse(localStorage.getItem('cart')) || [];
	cartItems = cartItems.filter(item => item.id !== productId);
	localStorage.setItem('cart', JSON.stringify(cartItems));
	updateCartDisplay();
	M.toast({html: 'Ürün sepetten çıkarıldı!', classes: 'rounded'});
}

// Ürün miktarını güncelle
function updateQuantity(productId, change) {
	let cartItems = JSON.parse(localStorage.getItem('cart')) || [];
	const item = cartItems.find(item => item.id === productId);
	if (item) {
		item.quantity += change;
		if (item.quantity <= 0) {
			removeFromCart(productId);
		} else {
			localStorage.setItem('cart', JSON.stringify(cartItems));
			updateCartDisplay();
		}
	}
}

// Sepet görüntüsünü güncelle
function updateCartDisplay() {
	const cartContent = document.querySelector('.cart-content');
	const cartCount = document.querySelector('.cart-count');
	const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
	
	// Sepet boşsa
	if (cartItems.length === 0) {
		cartContent.innerHTML = '<p class="center-align">Sepetiniz boş</p>';
		cartCount.style.display = 'none';
		return;
	}
	
	// Sepet sayısını güncelle
	const totalItems = cartItems.reduce((sum, item) => sum + item.quantity, 0);
	cartCount.textContent = totalItems;
	cartCount.style.display = 'block';
	
	// Sepet içeriğini güncelle
	let html = '<div class="cart-items">';
	let total = 0;
	
	cartItems.forEach(item => {
		const itemTotal = item.price * item.quantity;
		total += itemTotal;
		
		html += `
			<div class="cart-item">
				<img src="${item.imageUrl}" alt="${item.name}" class="cart-item-image">
				<div class="cart-item-details">
					<h6>${item.name}</h6>
					<div class="cart-item-price">${item.price.toFixed(2)} ₺</div>
					<div class="cart-item-quantity">
						<button onclick="updateQuantity(${item.id}, -1)" class="btn-small waves-effect waves-light">
							<i class="material-icons">remove</i>
						</button>
						<span>${item.quantity}</span>
						<button onclick="updateQuantity(${item.id}, 1)" class="btn-small waves-effect waves-light">
							<i class="material-icons">add</i>
						</button>
						<button onclick="removeFromCart(${item.id})" class="btn-small waves-effect waves-light red">
							<i class="material-icons">delete</i>
						</button>
					</div>
				</div>
			</div>
		`;
	});
	
	html += `
		<div class="cart-total">
			<h5>Toplam: ${total.toFixed(2)} ₺</h5>
			<button onclick="openOrderModal()" class="btn waves-effect waves-light">
				Siparişi Tamamla
			</button>
		</div>`;
	
	cartContent.innerHTML = html;
}

// Sepet panelini aç/kapat
function toggleCart() {
	const panel = document.getElementById('cart-panel');
	const overlay = document.getElementById('cart-overlay');
	panel.classList.toggle('active');
	overlay.classList.toggle('active');
}

// Sipariş için masaları yükle
function loadOrderTables() {
	fetch('/api/tables')
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.json();
		})
		.then(tables => {
			const tableSelect = document.getElementById('tableSelect');
			if (!tableSelect) {
				console.error('Table select element not found');
				return;
			}

			// Select'i temizle
			tableSelect.innerHTML = '<option value="" disabled selected>Masa Seçiniz</option>';

			// Masaları ekle
			tables.forEach(table => {
				tableSelect.innerHTML += `
					<option value="${table.id}">Masa ${table.tableNumber} (${table.capacity} Kişilik)</option>
				`;
			});

			// Materialize select'i yenile
			M.FormSelect.init(tableSelect);
		})
		.catch(error => {
			console.error('Masalar yüklenirken hata:', error);
			M.toast({html: 'Masalar yüklenirken bir hata oluştu!'});
		});
}

// Sipariş modalını aç
function openOrderModal() {
	// Sepet boş kontrolü
	const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
	if (cartItems.length === 0) {
		M.toast({html: 'Sepetiniz boş!'});
		return;
	}

	// Modal'ı başlat ve aç
	const modal = document.getElementById('orderForm');
	if (!M.Modal.getInstance(modal)) {
		M.Modal.init(modal);
	}
	M.Modal.getInstance(modal).open();

	// Masaları yükle
	loadOrderTables();

	// Sipariş özetini göster
	updateOrderSummary();
}

// Sipariş özetini güncelle
function updateOrderSummary() {
	const orderItems = document.getElementById('orderItems');
	const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
	let total = 0;

	let html = '<div class="order-items">';
	cartItems.forEach(item => {
		const itemTotal = item.price * item.quantity;
		total += itemTotal;
		html += `
			<div class="order-item">
				<span>${item.name}</span>
				<span>${item.quantity} adet</span>
				<span>${itemTotal.toFixed(2)} ₺</span>
			</div>
		`;
	});
	html += '</div>';

	orderItems.innerHTML = html;
	document.getElementById('orderTotal').textContent = total.toFixed(2);
}

// Sepet paneli fonksiyonları
document.addEventListener('DOMContentLoaded', function() {
	const cartPanel = document.getElementById('cart-panel');
	const cartOverlay = document.getElementById('cart-overlay');
	const cartTrigger = document.querySelector('[data-target="slide-out-right"]');
	const closeCartBtn = document.querySelector('.close-cart');

	function openCart(e) {
		if (e) e.preventDefault();
		cartPanel.classList.add('active');
		cartOverlay.classList.add('active');
		document.body.style.overflow = 'hidden';
	}

	function closeCart(e) {
		if (e) e.preventDefault();
		cartPanel.classList.remove('active');
		cartOverlay.classList.remove('active');
		document.body.style.overflow = '';
	}

	// Sepet ikonuna tıklama
	if (cartTrigger) {
		cartTrigger.addEventListener('click', openCart);
	}

	// Kapatma butonuna tıklama
	if (closeCartBtn) {
		closeCartBtn.addEventListener('click', closeCart);
	}

	// Overlay'e tıklama
	if (cartOverlay) {
		cartOverlay.addEventListener('click', closeCart);
	}

	// ESC tuşuna basma
	document.addEventListener('keydown', function(e) {
		if (e.key === 'Escape' && cartPanel.classList.contains('active')) {
			closeCart();
		}
	});
});

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
		items: cartItems
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
			throw new Error('Sipariş oluşturulurken bir hata oluştu!');
		}
		return response.json();
	})
	.then(data => {
		M.toast({html: 'Siparişiniz başarıyla alındı!'});
		// Sepeti temizle
		localStorage.removeItem('cart');
		// Sepet görüntüsünü güncelle
		updateCartDisplay();
		// Modalı kapat
		const modal = M.Modal.getInstance(document.getElementById('orderForm'));
		modal.close();
	})
	.catch(error => {
		console.error('Sipariş hatası:', error);
		M.toast({html: 'Sipariş oluşturulurken bir hata oluştu!'});
	});
}