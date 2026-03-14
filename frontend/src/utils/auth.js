export const AUTH_STORAGE_KEY = 'excel_auth'

export const saveAuth = (payload) => {
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(payload))
  localStorage.setItem('apiKey', payload.apiKey)
}

export const clearAuth = () => {
  localStorage.removeItem(AUTH_STORAGE_KEY)
  localStorage.removeItem('apiKey')
}

export const getAuth = () => {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY)
  if (!raw) return null
  try {
    const parsed = JSON.parse(raw)
    if (!parsed.expiresAt || Date.now() > parsed.expiresAt) {
      clearAuth()
      return null
    }
    return parsed
  } catch (e) {
    clearAuth()
    return null
  }
}

export const isAuthenticated = () => Boolean(getAuth())
