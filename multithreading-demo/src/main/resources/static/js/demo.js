// JavaScript for Multithreading Demo

// Global variables
let currentDemo = null;
let isRunning = false;

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    console.log('Multithreading Demo initialized');
    showWelcomeMessage();
});

// Show welcome message
function showWelcomeMessage() {
    const resultsDiv = document.getElementById('results');
    resultsDiv.innerHTML = `
        <div class="text-center text-muted">
            <h5>Welcome to Multithreading & Concurrency Demos</h5>
            <p>Select any demo above to see how different concurrency concepts work in Java.</p>
            <p>Each demo will show detailed logs of thread execution and synchronization.</p>
        </div>
    `;
}

// Run a demo with parameters
function runDemo(demoType, paramId = null) {
    if (isRunning) {
        showError('A demo is already running. Please wait for it to complete.');
        return;
    }

    const paramValue = paramId ? document.getElementById(paramId).value : null;
    runDemoInternal(demoType, paramValue);
}

// Run deadlock demo with fix option
function runDeadlockDemo() {
    if (isRunning) {
        showError('A demo is already running. Please wait for it to complete.');
        return;
    }

    const fix = document.getElementById('deadlockFix').checked;
    runDemoInternal('deadlock', fix);
}

// Run CyclicBarrier demo with multiple parameters
function runCyclicBarrierDemo() {
    if (isRunning) {
        showError('A demo is already running. Please wait for it to complete.');
        return;
    }

    const threads = document.getElementById('cbThreads').value;
    const phases = document.getElementById('cbPhases').value;
    const params = `${threads},${phases}`;
    runDemoInternal('cyclic-barrier', params);
}

// Run ReadWriteLock demo with multiple parameters
function runReadWriteLockDemo() {
    if (isRunning) {
        showError('A demo is already running. Please wait for it to complete.');
        return;
    }

    const readers = document.getElementById('rwlReaders').value;
    const writers = document.getElementById('rwlWriters').value;
    const params = `${readers},${writers}`;
    runDemoInternal('read-write-lock', params);
}

// Run ThreadPool demo with type and task count
function runThreadPoolDemo() {
    if (isRunning) {
        showError('A demo is already running. Please wait for it to complete.');
        return;
    }

    const type = document.getElementById('tpType').value;
    const tasks = document.getElementById('tpTasks').value;
    const params = `${type},${tasks}`;
    runDemoInternal('thread-pool', params);
}

// Internal function to run demo
function runDemoInternal(demoType, paramValue) {
    if (isRunning) {
        showError('A demo is already running. Please wait for it to complete.');
        return;
    }

    isRunning = true;
    currentDemo = demoType;
    
    showLoading();
    
    // Build URL with parameters
    let url = `/demo/${demoType}`;
    const params = new URLSearchParams();
    
    // Add parameters based on demo type
    switch (demoType) {
        case 'odd-even':
            params.append('maxNumber', paramValue || '20');
            break;
        case 'producer-consumer':
            params.append('itemCount', paramValue || '10');
            break;
        case 'deadlock':
            params.append('useFix', paramValue || 'false');
            break;
        case 'singleton':
            params.append('threadCount', paramValue || '5');
            break;
        case 'countdown-latch':
            params.append('workerCount', paramValue || '4');
            break;
        case 'cyclic-barrier':
            if (paramValue) {
                const [threadCount, phases] = paramValue.split(',');
                params.append('threadCount', threadCount);
                params.append('phases', phases);
            } else {
                params.append('threadCount', '3');
                params.append('phases', '2');
            }
            break;
        case 'read-write-lock':
            if (paramValue) {
                const [readerCount, writerCount] = paramValue.split(',');
                params.append('readerCount', readerCount);
                params.append('writerCount', writerCount);
            } else {
                params.append('readerCount', '3');
                params.append('writerCount', '2');
            }
            break;
        case 'thread-pool':
            if (paramValue) {
                const [poolType, taskCount] = paramValue.split(',');
                params.append('poolType', poolType);
                params.append('taskCount', taskCount);
            } else {
                params.append('poolType', 'fixed');
                params.append('taskCount', '6');
            }
            break;
    }
    
    if (params.toString()) {
        url += '?' + params.toString();
    }
    
    // Make API call
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        displayResults(data);
    })
    .catch(error => {
        console.error('Error:', error);
        showError('Failed to run demo: ' + error.message);
    })
    .finally(() => {
        isRunning = false;
        currentDemo = null;
    });
}

// Show loading indicator
function showLoading() {
    const resultsDiv = document.getElementById('results');
    resultsDiv.innerHTML = `
        <div class="loading">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Running demo...</span>
            </div>
            <p class="mt-3">Running ${getDemoName(currentDemo)} demo...</p>
        </div>
    `;
}

// Display demo results
function displayResults(data) {
    const resultsDiv = document.getElementById('results');
    
    let html = '';
    
    // Status message
    if (data.status) {
        const statusClass = data.status.toLowerCase().includes('error') ? 'status-error' : 
                           data.status.toLowerCase().includes('success') ? 'status-success' : 'status-info';
        html += `<div class="status-message ${statusClass}">${data.status}</div>`;
    }
    
    // Logs
    if (data.logs && data.logs.length > 0) {
        html += '<div class="logs-container">';
        data.logs.forEach((log, index) => {
            const logClass = getLogClass(log);
            html += `<div class="log-entry ${logClass}" style="animation-delay: ${index * 0.1}s">${escapeHtml(log)}</div>`;
        });
        html += '</div>';
    } else {
        html += '<p class="text-muted text-center">No logs available for this demo.</p>';
    }
    
    resultsDiv.innerHTML = html;
    
    // Scroll to bottom of results
    resultsDiv.scrollTop = resultsDiv.scrollHeight;
}

// Show error message
function showError(message) {
    const resultsDiv = document.getElementById('results');
    resultsDiv.innerHTML = `
        <div class="status-message status-error">
            <strong>Error:</strong> ${escapeHtml(message)}
        </div>
    `;
}

// Get demo name for display
function getDemoName(demoType) {
    const names = {
        'odd-even': 'Odd/Even Number Printing',
        'producer-consumer': 'Producer/Consumer',
        'deadlock': 'Deadlock',
        'singleton': 'Thread-safe Singleton',
        'countdown-latch': 'CountDownLatch',
        'cyclic-barrier': 'CyclicBarrier',
        'read-write-lock': 'ReadWriteLock',
        'completable-future': 'CompletableFuture',
        'thread-pool': 'ThreadPool'
    };
    return names[demoType] || demoType;
}

// Get CSS class for log entry based on content
function getLogClass(log) {
    const logLower = log.toLowerCase();
    
    if (logLower.includes('error') || logLower.includes('exception')) {
        return 'error';
    } else if (logLower.includes('success') || logLower.includes('completed')) {
        return 'success';
    } else if (logLower.includes('warning') || logLower.includes('wait')) {
        return 'warning';
    } else if (logLower.includes('debug') || logLower.includes('trace')) {
        return 'debug';
    } else {
        return 'info';
    }
}

// Escape HTML to prevent XSS
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Utility function to format timestamps
function formatTimestamp(timestamp) {
    return new Date(timestamp).toLocaleTimeString();
}

// Add keyboard shortcuts
document.addEventListener('keydown', function(event) {
    // Ctrl/Cmd + R to refresh results
    if ((event.ctrlKey || event.metaKey) && event.key === 'r') {
        event.preventDefault();
        showWelcomeMessage();
    }
    
    // Escape to stop current demo (if possible)
    if (event.key === 'Escape' && isRunning) {
        console.log('Demo is running, cannot stop via keyboard');
    }
});

// Add tooltips for better UX
document.addEventListener('DOMContentLoaded', function() {
    // Initialize Bootstrap tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});
