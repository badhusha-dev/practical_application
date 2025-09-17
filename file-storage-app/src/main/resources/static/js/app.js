// Custom JavaScript for File Storage App

document.addEventListener('DOMContentLoaded', function() {
    // Initialize theme toggle
    initializeThemeToggle();
    
    // Initialize file upload
    initializeFileUpload();
    
    // Initialize tooltips
    initializeTooltips();
    
    // Initialize auto-dismiss alerts
    initializeAutoDismissAlerts();
});

// Theme Toggle Functionality
function initializeThemeToggle() {
    const themeToggle = document.getElementById('themeToggle');
    const themeIcon = document.getElementById('themeIcon');
    
    if (themeToggle && themeIcon) {
        themeToggle.addEventListener('click', function() {
            const currentTheme = document.documentElement.getAttribute('data-bs-theme');
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            
            document.documentElement.setAttribute('data-bs-theme', newTheme);
            localStorage.setItem('theme', newTheme);
            
            // Update icon
            themeIcon.className = newTheme === 'dark' ? 'bi bi-moon' : 'bi bi-sun';
        });
        
        // Set initial icon based on current theme
        const currentTheme = document.documentElement.getAttribute('data-bs-theme');
        themeIcon.className = currentTheme === 'dark' ? 'bi bi-moon' : 'bi bi-sun';
    }
}

// File Upload Functionality
function initializeFileUpload() {
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('file');
    const uploadForm = document.getElementById('uploadForm');
    const uploadProgress = document.getElementById('uploadProgress');
    const uploadBtn = document.getElementById('uploadBtn');
    
    if (dropZone && fileInput) {
        // Click to browse
        dropZone.addEventListener('click', function() {
            fileInput.click();
        });
        
        // Drag and drop events
        dropZone.addEventListener('dragover', function(e) {
            e.preventDefault();
            dropZone.classList.add('border-primary');
            dropZone.style.backgroundColor = 'rgba(13, 110, 253, 0.1)';
        });
        
        dropZone.addEventListener('dragleave', function(e) {
            e.preventDefault();
            dropZone.classList.remove('border-primary');
            dropZone.style.backgroundColor = '';
        });
        
        dropZone.addEventListener('drop', function(e) {
            e.preventDefault();
            dropZone.classList.remove('border-primary');
            dropZone.style.backgroundColor = '';
            
            const files = e.dataTransfer.files;
            if (files.length > 0) {
                fileInput.files = files;
                updateFilePreview(files[0]);
            }
        });
        
        // File input change
        fileInput.addEventListener('change', function() {
            if (this.files.length > 0) {
                updateFilePreview(this.files[0]);
            }
        });
    }
    
    // Form submission with progress
    if (uploadForm && uploadProgress && uploadBtn) {
        uploadForm.addEventListener('submit', function(e) {
            const fileInput = document.getElementById('file');
            if (fileInput.files.length === 0) {
                e.preventDefault();
                showAlert('Please select a file to upload', 'warning');
                return;
            }
            
            // Show progress bar
            uploadProgress.style.display = 'block';
            uploadBtn.disabled = true;
            uploadBtn.innerHTML = '<i class="bi bi-hourglass-split me-2"></i>Uploading...';
            
            // Simulate progress (in real app, you'd use XMLHttpRequest with progress events)
            simulateUploadProgress();
        });
    }
}

// Update file preview
function updateFilePreview(file) {
    const dropZone = document.getElementById('dropZone');
    if (dropZone) {
        const fileIcon = getFileIcon(file.type);
        const fileSize = formatFileSize(file.size);
        
        dropZone.innerHTML = `
            <i class="bi ${fileIcon} display-4 text-primary"></i>
            <p class="text-primary mt-2 fw-bold">${file.name}</p>
            <small class="text-muted">${fileSize}</small>
        `;
    }
}

// Get file icon based on type
function getFileIcon(contentType) {
    if (contentType.startsWith('image/')) return 'bi-image';
    if (contentType.startsWith('video/')) return 'bi-play-circle';
    if (contentType.startsWith('audio/')) return 'bi-music-note';
    if (contentType.includes('pdf')) return 'bi-file-pdf';
    if (contentType.includes('word') || contentType.includes('document')) return 'bi-file-word';
    if (contentType.includes('excel') || contentType.includes('spreadsheet')) return 'bi-file-excel';
    if (contentType.includes('powerpoint') || contentType.includes('presentation')) return 'bi-file-ppt';
    if (contentType.includes('zip') || contentType.includes('rar') || contentType.includes('archive')) return 'bi-file-zip';
    return 'bi-file-earmark';
}

// Format file size
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// Simulate upload progress
function simulateUploadProgress() {
    const progressBar = document.querySelector('#uploadProgress .progress-bar');
    let progress = 0;
    
    const interval = setInterval(function() {
        progress += Math.random() * 15;
        if (progress > 100) progress = 100;
        
        if (progressBar) {
            progressBar.style.width = progress + '%';
            progressBar.textContent = Math.round(progress) + '%';
        }
        
        if (progress >= 100) {
            clearInterval(interval);
            setTimeout(function() {
                // Form will submit normally after this
            }, 500);
        }
    }, 200);
}

// Initialize tooltips
function initializeTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

// Initialize auto-dismiss alerts
function initializeAutoDismissAlerts() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000); // Auto-dismiss after 5 seconds
    });
}

// Show alert function
function showAlert(message, type = 'info') {
    const alertContainer = document.querySelector('.container-fluid .py-4');
    if (alertContainer) {
        const alertHtml = `
            <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                <i class="bi bi-${getAlertIcon(type)} me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
        alertContainer.insertAdjacentHTML('afterbegin', alertHtml);
        
        // Auto-dismiss after 5 seconds
        setTimeout(function() {
            const alert = alertContainer.querySelector('.alert');
            if (alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        }, 5000);
    }
}

// Get alert icon based on type
function getAlertIcon(type) {
    const icons = {
        'success': 'check-circle',
        'danger': 'exclamation-triangle',
        'warning': 'exclamation-triangle',
        'info': 'info-circle'
    };
    return icons[type] || 'info-circle';
}

// File operations
function deleteFile(fileId, filename) {
    if (confirm(`Are you sure you want to delete "${filename}"? This action cannot be undone.`)) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `/files/delete/${fileId}`;
        document.body.appendChild(form);
        form.submit();
    }
}

function shareFile(fileId) {
    // Open share modal
    const shareModal = new bootstrap.Modal(document.getElementById('shareModal'));
    document.getElementById('shareModal').setAttribute('data-file-id', fileId);
    shareModal.show();
}

function createShare() {
    const modal = document.getElementById('shareModal');
    const fileId = modal.getAttribute('data-file-id');
    const expiresAt = document.getElementById('expiresAt').value;
    const maxDownloads = document.getElementById('maxDownloads').value;
    
    // Create share request
    const formData = new FormData();
    formData.append('fileId', fileId);
    if (expiresAt) formData.append('expiresAt', expiresAt);
    if (maxDownloads) formData.append('maxDownloads', maxDownloads);
    
    fetch('/api/files/share', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showAlert('Share link created successfully!', 'success');
            bootstrap.Modal.getInstance(modal).hide();
        } else {
            showAlert(data.message || 'Failed to create share', 'danger');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showAlert('Failed to create share', 'danger');
    });
}

// Search functionality
function performSearch() {
    const searchInput = document.querySelector('input[name="search"]');
    if (searchInput) {
        const searchTerm = searchInput.value.trim();
        if (searchTerm.length > 0) {
            // Add loading state
            const searchBtn = document.querySelector('button[type="submit"]');
            if (searchBtn) {
                searchBtn.disabled = true;
                searchBtn.innerHTML = '<i class="bi bi-hourglass-split"></i>';
            }
        }
    }
}

// Storage usage visualization
function updateStorageUsage() {
    const progressBars = document.querySelectorAll('.storage-progress .progress-bar');
    progressBars.forEach(function(bar) {
        const percentage = parseFloat(bar.style.width) || 0;
        if (percentage > 80) {
            bar.classList.add('bg-danger');
        } else if (percentage > 60) {
            bar.classList.add('bg-warning');
        } else {
            bar.classList.add('bg-success');
        }
    });
}

// Initialize storage usage on page load
document.addEventListener('DOMContentLoaded', function() {
    updateStorageUsage();
});

// Utility functions
function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(function() {
        showAlert('Copied to clipboard!', 'success');
    }).catch(function() {
        showAlert('Failed to copy to clipboard', 'danger');
    });
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
}

// File type validation
function validateFileType(file) {
    const allowedTypes = [
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'text/plain',
        'image/jpeg',
        'image/png',
        'image/gif',
        'video/mp4',
        'audio/mpeg',
        'application/zip',
        'application/x-rar-compressed'
    ];
    
    return allowedTypes.includes(file.type);
}

// File size validation
function validateFileSize(file, maxSize = 100 * 1024 * 1024) { // 100MB default
    return file.size <= maxSize;
}

// Export functions for global use
window.FileStorageApp = {
    showAlert,
    deleteFile,
    shareFile,
    createShare,
    copyToClipboard,
    formatFileSize,
    formatDate,
    validateFileType,
    validateFileSize
};
