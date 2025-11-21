package com.techmate.techmate.service;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    /**
     * Genera la plantilla HTML para el email de verificación de cuenta
     * con INLINE CSS para máxima compatibilidad con clientes de email
     */
    public String generateVerificationEmail(String userName, String verificationUrl) {
        return """
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verificación de Cuenta - TechShare</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background: linear-gradient(135deg, #eff6ff 0%%, #e0e7ff 50%%, #f3e8ff 100%%); padding: 40px 20px;">
    <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="background: linear-gradient(135deg, #eff6ff 0%%, #e0e7ff 50%%, #f3e8ff 100%%);">
        <tr>
            <td align="center" style="padding: 40px 20px;">
                <!-- Email Container -->
                <table width="500" cellpadding="0" cellspacing="0" border="0" style="max-width: 500px; background: #ffffff; border-radius: 16px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1); overflow: hidden;">
                    
                    <!-- Content -->
                    <tr>
                        <td style="padding: 48px 40px; text-align: center;">
                            
                            <!-- Logo Section -->
                            <div style="margin-bottom: 32px;">
                                <div style="font-size: 36px; font-weight: bold; color: #1E2A5E; margin-bottom: 8px; letter-spacing: 1px;">
                                    TechShare
                                </div>
                                <div style="color: #6B7280; font-size: 14px;">
                                    Plataforma de Gestión de Materiales Educativos
                                </div>
                            </div>
                            
                            <!-- Icon -->
                            <div style="margin-bottom: 24px;">
                                <table width="80" height="80" cellpadding="0" cellspacing="0" border="0" align="center" style="background: linear-gradient(135deg, #1E2A5E 0%%, #2D3E7C 100%%); border-radius: 50%%;">
                                    <tr>
                                        <td align="center" valign="middle" style="font-size: 40px; color: #ffffff;">
                                            📧
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            
                            <!-- Title -->
                            <h1 style="font-size: 28px; color: #1E2A5E; font-weight: bold; margin: 0 0 16px 0;">
                                VERIFICACIÓN DE CUENTA
                            </h1>
                            
                            <!-- Message -->
                            <p style="color: #4B5563; font-size: 16px; line-height: 1.6; margin: 0 0 32px 0;">
                                Hola <strong>%s</strong>,<br><br>
                                Bienvenido a TechShare. Para activar tu cuenta y comenzar a usar la plataforma, 
                                haz clic en el botón de abajo para verificar tu correo electrónico.
                            </p>
                            
                            <!-- Button -->
                            <div style="margin: 32px 0;">
                                <a href="%s" style="display: inline-block; background: #1E2A5E; color: #ffffff !important; text-decoration: none; padding: 14px 40px; border-radius: 8px; font-size: 16px; font-weight: 600; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);">
                                    VERIFICAR MI CUENTA
                                </a>
                            </div>
                            
                            <!-- Warning -->
                            <table width="100%%" cellpadding="16" cellspacing="0" border="0" style="background: #FEF3C7; border-left: 4px solid #F59E0B; border-radius: 8px; margin: 24px 0;">
                                <tr>
                                    <td style="text-align: left;">
                                        <p style="color: #92400E; font-size: 14px; line-height: 1.6; margin: 0;">
                                            <strong>⚠ Importante:</strong> Este enlace expirará en 24 horas. 
                                            Si no verificas tu cuenta antes de ese tiempo, deberás registrarte nuevamente.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            
                        </td>
                    </tr>
                    
                    <!-- Footer -->
                    <tr>
                        <td style="background: #F9FAFB; padding: 24px 40px; text-align: center; border-top: 1px solid #E5E7EB;">
                            <p style="color: #6B7280; font-size: 12px; line-height: 1.6; margin: 0;">
                                Si no solicitaste esta cuenta, puedes ignorar este correo de forma segura.
                                <br><br>
                                © 2025 TechShare. Todos los derechos reservados.
                                <br>
                                Este es un correo electrónico automático, por favor no respondas a este mensaje.
                            </p>
                        </td>
                    </tr>
                    
                </table>
            </td>
        </tr>
    </table>
</body>
</html>
                """.formatted(userName, verificationUrl);
    }

    /**
     * Genera la plantilla HTML para confirmación de verificación exitosa
     * con INLINE CSS para máxima compatibilidad con clientes de email
     */
    public String generateWelcomeEmail(String userName) {
        return """
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>¡Bienvenido a TechShare!</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background: linear-gradient(135deg, #eff6ff 0%%, #e0e7ff 50%%, #f3e8ff 100%%); padding: 40px 20px;">
    <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="background: linear-gradient(135deg, #eff6ff 0%%, #e0e7ff 50%%, #f3e8ff 100%%);">
        <tr>
            <td align="center" style="padding: 40px 20px;">
                <!-- Email Container -->
                <table width="500" cellpadding="0" cellspacing="0" border="0" style="max-width: 500px; background: #ffffff; border-radius: 16px; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1); overflow: hidden;">
                    
                    <!-- Content -->
                    <tr>
                        <td style="padding: 48px 40px; text-align: center;">
                            
                            <!-- Logo -->
                            <div style="margin-bottom: 32px;">
                                <div style="font-size: 36px; font-weight: bold; color: #1E2A5E; margin-bottom: 8px; letter-spacing: 1px;">
                                    TechShare
                                </div>
                            </div>
                            
                            <!-- Success Icon -->
                            <div style="margin-bottom: 24px;">
                                <table width="80" height="80" cellpadding="0" cellspacing="0" border="0" align="center" style="background: linear-gradient(135deg, #10B981 0%%, #059669 100%%); border-radius: 50%%;">
                                    <tr>
                                        <td align="center" valign="middle" style="font-size: 40px; color: #ffffff;">
                                            ✓
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            
                            <!-- Title -->
                            <h1 style="font-size: 28px; color: #1E2A5E; font-weight: bold; margin: 0 0 16px 0;">
                                ¡CUENTA VERIFICADA!
                            </h1>
                            
                            <!-- Message -->
                            <p style="color: #4B5563; font-size: 16px; line-height: 1.6; margin: 0 0 32px 0;">
                                <strong>Hola %s</strong>,<br><br>
                                Tu cuenta en TechShare ha sido verificada exitosamente. 
                                Ya puedes iniciar sesión y comenzar a usar todas las funcionalidades de la plataforma.
                            </p>
                            
                            <!-- Button -->
                            <div style="margin: 32px 0;">
                                <a href="http://localhost:3000/login" style="display: inline-block; background: #1E2A5E; color: #ffffff !important; text-decoration: none; padding: 14px 40px; border-radius: 8px; font-size: 16px; font-weight: 600; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);">
                                    INICIAR SESIÓN
                                </a>
                            </div>
                            
                        </td>
                    </tr>
                    
                    <!-- Footer -->
                    <tr>
                        <td style="background: #F9FAFB; padding: 24px 40px; text-align: center; border-top: 1px solid #E5E7EB;">
                            <p style="color: #6B7280; font-size: 12px; line-height: 1.6; margin: 0;">
                                © 2025 TechShare. Todos los derechos reservados.
                            </p>
                        </td>
                    </tr>
                    
                </table>
            </td>
        </tr>
    </table>
</body>
</html>
                """.formatted(userName);
    }
}


