const apiBase = import.meta.env.VITE_API_BASE_URL?.replace(/\/$/, '');

export function hasApi() {
  return Boolean(apiBase);
}

async function request(path, options = {}) {
  if (!apiBase) {
    throw new Error('Backend API is not configured.');
  }

  const response = await fetch(`${apiBase}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers ?? {})
    },
    ...options
  });

  if (!response.ok) {
    let serverMessage = '';
    try {
      const body = await response.json();
      serverMessage = body.detail || body.message || '';
    } catch {
      // Some error responses do not include JSON.
    }

    const message =
      response.status === 409
        ? serverMessage || 'That time is no longer available. Try another slot.'
        : response.status === 404
          ? 'We could not find that record. Check the confirmation number and try again.'
          : response.status === 400
            ? serverMessage || 'Some booking details are invalid. Check the form and try again.'
            : response.status >= 500
              ? 'The booking service is temporarily unavailable. Please try again.'
              : serverMessage || 'Something went wrong. Please try again.';

    throw new Error(message);
  }

  if (response.status === 204) return null;
  return response.json();
}

export function getServices() {
  return request('/api/services');
}

export function getGroomers() {
  return request('/api/groomers');
}

export function createAppointment(payload) {
  return request('/api/appointments', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function createClient(payload) {
  return request('/api/clients', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function createPet(payload) {
  return request('/api/pets', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function getAppointment(id) {
  return request(`/api/appointments/${id}`);
}

export function updateAppointmentStatus(id, status) {
  return request(`/api/appointments/${id}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status })
  });
}

export function rescheduleAppointment(id, payload) {
  return request(`/api/appointments/${id}/schedule`, {
    method: 'PATCH',
    body: JSON.stringify(payload)
  });
}
