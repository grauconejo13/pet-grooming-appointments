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
    const message =
      response.status === 409
        ? 'That time is no longer available.'
        : response.status === 404
          ? 'The requested record was not found.'
          : 'Something went wrong. Please try again.';
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
