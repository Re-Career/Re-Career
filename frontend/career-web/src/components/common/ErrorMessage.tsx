interface ErrorMessageProps {
  message?: string
  error?: string
  clientError?: string
}

const ErrorMessage = ({ message, error, clientError }: ErrorMessageProps) => {
  if (!message && !error && !clientError) return null

  return (
    <div
      className={'mx-4 mb-4 rounded-xl bg-red-100 p-3 text-sm text-gray-800'}
    >
      {message && <p>{message}</p>}
      {error && <p className="font-semibold text-red-800">{error}</p>}
      {clientError && (
        <p className="font-semibold text-red-800">{clientError}</p>
      )}
    </div>
  )
}

export default ErrorMessage
